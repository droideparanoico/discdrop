package io.discdrop.service;

import io.discdrop.mbz.MusicBrainzService;
import io.discdrop.mbz.dto.ArtistSearchResult;
import io.discdrop.persistence.ArtistTypeSetting;
import io.discdrop.persistence.FollowedArtist;
import io.discdrop.persistence.ReleaseGroupEntity;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ArtistService {

    @Inject
    SettingsService settingsService;

    @Inject
    MusicBrainzService mbzService;

    @Transactional
    public FollowedArtist follow(ArtistSearchResult.ArtistDto dto) {
        if (FollowedArtist.existsByMbid(dto.id)) {
            return FollowedArtist.findByMbid(dto.id);
        }
        FollowedArtist artist = new FollowedArtist();
        artist.mbid = dto.id;
        artist.name = dto.name;
        artist.sortName = dto.name;
        artist.disambiguation = dto.disambiguation;
        artist.areaName = dto.area != null ? dto.area.name : null;
        artist.type = dto.type;
        artist.country = dto.country;
        artist.followedAt = Instant.now();
        artist.persist();

        Set<String> defaults = settingsService.getDefaultPrimaryTypes();
        for (String type : SettingsService.ALL_PRIMARY_TYPES) {
            ArtistTypeSetting s = new ArtistTypeSetting();
            s.artist = artist;
            s.primaryType = type;
            s.enabled = defaults.contains(type);
            s.persist();
        }
        return artist;
    }

    @Transactional
    public void unfollow(String mbid) {
        FollowedArtist artist = FollowedArtist.findByMbid(mbid);
        if (artist == null) {
            return;
        }
        ArtistTypeSetting.deleteByArtist(artist);
        ReleaseGroupEntity.deleteByArtist(artist);
        artist.delete();
    }

    @Transactional
    public void setTypeEnabled(String mbid, String primaryType, boolean enabled) {
        FollowedArtist artist = FollowedArtist.findByMbid(mbid);
        if (artist == null) {
            return;
        }
        for (ArtistTypeSetting s : ArtistTypeSetting.findByArtist(artist)) {
            if (s.primaryType.equalsIgnoreCase(primaryType)) {
                s.enabled = enabled;
            }
        }
    }

    @Transactional
    public FollowedArtist findArtist(String mbid) {
        return FollowedArtist.findByMbid(mbid);
    }

    @Transactional
    public Set<String> enabledTypes(String mbid) {
        FollowedArtist artist = FollowedArtist.findByMbid(mbid);
        if (artist == null) {
            return Set.of();
        }
        return ArtistTypeSetting.findByArtist(artist).stream()
                .filter(s -> s.enabled)
                .map(s -> s.primaryType)
                .collect(Collectors.toSet());
    }

    @Transactional
    public List<FollowedArtist> allFollowed() {
        return FollowedArtist.listAll(Sort.by("name"));
    }

    @Transactional
    public List<ArtistTypeSetting> settingsFor(FollowedArtist artist) {
        return ArtistTypeSetting.findByArtist(artist);
    }

    public List<ArtistSearchResult.ArtistDto> search(String query) {
        return mbzService.searchArtists(query, 10);
    }
}
