package io.discdrop.service;

import io.discdrop.mbz.MusicBrainzService;
import io.discdrop.mbz.dto.ReleaseGroupBrowseResult;
import io.discdrop.persistence.FollowedArtist;
import io.discdrop.persistence.ReleaseGroupRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class SyncService {

    @Inject
    MusicBrainzService mbzService;

    @Inject
    ReleaseGroupRepository repo;

    @Inject
    ArtistService artistService;

    @Inject
    SettingsService settingsService;

    private final Set<String> syncing = ConcurrentHashMap.newKeySet();

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "discdrop-sync");
                t.setDaemon(true);
                return t;
            });

    private volatile ScheduledFuture<?> currentTask;

    void onStart(@Observes StartupEvent event) {
        reschedule();
    }

    @PreDestroy
    void shutdown() {
        executor.shutdownNow();
    }

    public synchronized void reschedule() {
        if (currentTask != null) {
            currentTask.cancel(false);
        }
        int hours = settingsService.getSyncScheduleHours();
        long period = TimeUnit.HOURS.toSeconds(hours);
        currentTask = executor.scheduleAtFixedRate(
                this::runSyncSafe, period, period, TimeUnit.SECONDS);
    }

    void runSyncSafe() {
        try {
            runSync();
        } catch (Exception e) {
            // keep the scheduler alive across failures
        }
    }

    public void runSync() {
        for (FollowedArtist artist : artistService.allFollowed()) {
            syncArtist(artist.mbid);
        }
    }

    public boolean isSyncing(String mbid) {
        return syncing.contains(mbid);
    }

    public boolean isAnySyncing() {
        return !syncing.isEmpty();
    }

    public void syncArtist(String mbid) {
        syncing.add(mbid);
        try {
            Set<String> enabledTypes = artistService.enabledTypes(mbid);
            String typeFilter = enabledTypes.isEmpty() ? null : String.join("|", enabledTypes);

            int limit = 100;
            int offset = 0;
            boolean more = true;
            while (more) {
                ReleaseGroupBrowseResult page = mbzService.browseReleaseGroups(mbid, typeFilter, limit, offset);
                if (page == null || page.releaseGroups == null || page.releaseGroups.isEmpty()) {
                    break;
                }
                repo.upsertPage(mbid, page.releaseGroups);
                offset += page.releaseGroups.size();
                more = page.releaseGroups.size() == limit && offset < page.count;
            }
            repo.markSynced(mbid);
        } finally {
            syncing.remove(mbid);
        }
    }

    public void resyncArtist(String mbid) {
        syncing.add(mbid);
        try {
            Set<String> enabledTypes = artistService.enabledTypes(mbid);
            String typeFilter = enabledTypes.isEmpty() ? null : String.join("|", enabledTypes);

            List<ReleaseGroupBrowseResult.ReleaseGroupDto> all = new ArrayList<>();
            int limit = 100;
            int offset = 0;
            boolean more = true;
            while (more) {
                ReleaseGroupBrowseResult page = mbzService.browseReleaseGroups(mbid, typeFilter, limit, offset);
                if (page == null || page.releaseGroups == null || page.releaseGroups.isEmpty()) {
                    break;
                }
                all.addAll(page.releaseGroups);
                offset += page.releaseGroups.size();
                more = page.releaseGroups.size() == limit && offset < page.count;
            }
            repo.replaceForArtist(mbid, all);
            repo.markSynced(mbid);
        } finally {
            syncing.remove(mbid);
        }
    }
}
