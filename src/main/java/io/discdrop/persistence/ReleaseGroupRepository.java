package io.discdrop.persistence;

import io.discdrop.mbz.dto.ReleaseGroupBrowseResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@ApplicationScoped
public class ReleaseGroupRepository {

    @Transactional
    public FollowedArtist findArtist(String mbid) {
        return FollowedArtist.findByMbid(mbid);
    }

    @Transactional
    public void upsertPage(String mbid, List<ReleaseGroupBrowseResult.ReleaseGroupDto> dtos) {
        FollowedArtist artist = FollowedArtist.findByMbid(mbid);
        if (artist == null) {
            return;
        }
        for (ReleaseGroupBrowseResult.ReleaseGroupDto rg : dtos) {
            upsertOne(artist, rg);
        }
    }

    @Transactional
    public void markSynced(String mbid) {
        FollowedArtist artist = FollowedArtist.findByMbid(mbid);
        if (artist != null) {
            artist.lastSyncedAt = Instant.now();
        }
    }

    @Transactional
    public void deleteByArtistMbid(String mbid) {
        FollowedArtist artist = FollowedArtist.findByMbid(mbid);
        if (artist != null) {
            ReleaseGroupEntity.deleteByArtist(artist);
        }
    }

    private void upsertOne(FollowedArtist artist, ReleaseGroupBrowseResult.ReleaseGroupDto rg) {
        ReleaseGroupEntity entity = ReleaseGroupEntity.findByMbid(rg.id);
        boolean isNew = entity == null;
        if (isNew) {
            entity = new ReleaseGroupEntity();
            entity.mbid = rg.id;
            entity.artist = artist;
            entity.discoveredAt = Instant.now();
        }
        entity.title = rg.title;
        entity.firstReleaseDateRaw = rg.firstReleaseDate;
        entity.firstReleaseDate = parseDate(rg.firstReleaseDate);
        entity.primaryType = rg.primaryType;
        entity.secondaryTypes = rg.secondaryTypes != null ? String.join(", ", rg.secondaryTypes) : null;
        entity.mbzUrl = "https://musicbrainz.org/release-group/" + rg.id;
        if (isNew) {
            entity.persist();
        }
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            if (raw.length() >= 10) {
                return LocalDate.parse(raw.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (raw.length() >= 7) {
                return LocalDate.parse(raw + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (raw.length() >= 4) {
                return LocalDate.parse(raw + "-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            return null;
        }
        return null;
    }
}
