package io.discdrop.resource;

import io.discdrop.persistence.FollowedArtist;
import io.discdrop.persistence.ReleaseGroupEntity;
import io.discdrop.service.FeedService;
import io.discdrop.service.SettingsService;
import io.discdrop.service.SyncService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

@Path("/")
public class PageResource {

    @Inject
    FeedService feedService;

    @Inject
    SyncService syncService;

    @Inject
    SettingsService settingsService;

    @Inject
    Template index;

    @Inject
    @Location("fragments/feed-list.html")
    Template fragments_feed_list;

    @Path("/feed")
    @GET
    @Transactional
    public TemplateInstance feedFragment(@QueryParam("offset") int offset) {
        if (offset < 0) {
            offset = 0;
        }
        boolean hideFuture = settingsService.isHideFutureFeed();
        List<ReleaseGroupEntity> rows = feedService.feedPage(offset, hideFuture);
        int nextOffset = offset + rows.size();
        boolean hasMore = rows.size() == feedService.pageSize();
        return fragments_feed_list.data("rows", rows)
                .data("nextOffset", nextOffset)
                .data("hasMore", hasMore)
                .data("pageSize", feedService.pageSize())
                .data("autoRefresh", false)
                .data("syncing", syncService.isAnySyncing());
    }

    @GET
    @Transactional
    public TemplateInstance index() {
        boolean hideFuture = settingsService.isHideFutureFeed();
        List<ReleaseGroupEntity> rows = feedService.feedPage(0, hideFuture);
        boolean hasMore = rows.size() == feedService.pageSize();
        return index.data("rows", rows)
                .data("nextOffset", rows.size())
                .data("hasMore", hasMore)
                .data("pageSize", feedService.pageSize())
                .data("autoRefresh", false)
                .data("syncing", syncService.isAnySyncing())
                .data("followedCount", FollowedArtist.count())
                .data("feedCount", ReleaseGroupEntity.count());
    }
}
