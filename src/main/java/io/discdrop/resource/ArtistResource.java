package io.discdrop.resource;

import io.discdrop.mbz.dto.ArtistSearchResult;
import io.discdrop.persistence.ArtistTypeSetting;
import io.discdrop.persistence.FollowedArtist;
import io.discdrop.persistence.ReleaseGroupEntity;
import io.discdrop.service.ArtistService;
import io.discdrop.service.FeedService;
import io.discdrop.service.SyncService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.List;

@Path("/artists")
public class ArtistResource {

    @Inject
    ArtistService artistService;

    @Inject
    SyncService syncService;

    @Inject
    FeedService feedService;

    @Inject
    @Location("fragments/feed-list.html")
    Template fragments_feed_list;

    @Inject
    @Location("fragments/artist-row.html")
    Template fragments_artist_row;

    @Inject
    Template artists;

    @POST
    @Path("/follow")
    public TemplateInstance follow(@FormParam("mbid") String mbid,
                                   @FormParam("name") String name,
                                   @FormParam("disambiguation") String disambiguation,
                                   @FormParam("area") String area,
                                   @FormParam("type") String type,
                                   @FormParam("country") String country) {
        ArtistSearchResult.ArtistDto dto = new ArtistSearchResult.ArtistDto();
        dto.id = mbid;
        dto.name = name;
        dto.disambiguation = disambiguation;
        dto.type = type;
        dto.country = country;
        if (area != null && !area.isBlank()) {
            dto.area = new ArtistSearchResult.ArtistDto.Area();
            dto.area.name = area;
        }
        FollowedArtist artist = artistService.follow(dto);
        syncService.syncArtist(artist.mbid);
        return feedFragment(0);
    }

    @DELETE
    @Path("/{mbid}")
    public TemplateInstance unfollow(@PathParam("mbid") String mbid) {
        artistService.unfollow(mbid);
        return feedFragment(0);
    }

    @GET
    public TemplateInstance artistsPage() {
        List<FollowedArtist> artistsList = artistService.allFollowed();
        return artists.data("artists", artistsList);
    }

    @GET
    @Path("/{mbid}/row")
    public TemplateInstance artistRow(@PathParam("mbid") String mbid) {
        FollowedArtist artist = artistService.findArtist(mbid);
        List<ArtistTypeSetting> settings = artist != null ? artistService.settingsFor(artist) : List.of();
        return fragments_artist_row.data("artist", artist)
                .data("settings", settings);
    }

    @POST
    @Path("/{mbid}/types")
    public TemplateInstance toggleType(@PathParam("mbid") String mbid,
                                       @FormParam("primaryType") String primaryType,
                                       @FormParam("enabled") boolean enabled) {
        FollowedArtist artist = artistService.findArtist(mbid);
        if (artist != null) {
            artistService.setTypeEnabled(artist, primaryType, enabled);
            syncService.resyncArtist(mbid);
        }
        return feedFragment(0);
    }

    private TemplateInstance feedFragment(int offset) {
        if (offset < 0) {
            offset = 0;
        }
        List<ReleaseGroupEntity> rows = feedService.feedPage(offset);
        int nextOffset = offset + rows.size();
        boolean hasMore = rows.size() == feedService.pageSize();
        return fragments_feed_list.data("rows", rows)
                .data("nextOffset", nextOffset)
                .data("hasMore", hasMore)
                .data("pageSize", feedService.pageSize());
    }
}
