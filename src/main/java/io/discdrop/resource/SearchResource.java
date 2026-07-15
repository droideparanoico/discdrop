package io.discdrop.resource;

import io.discdrop.mbz.dto.ArtistSearchResult;
import io.discdrop.persistence.FollowedArtist;
import io.discdrop.service.ArtistService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

@Path("/search")
public class SearchResource {

    @Inject
    ArtistService artistService;

    @Inject
    @Location("fragments/search-dropdown.html")
    Template fragments_search_dropdown;

    @Path("/artists")
    @GET
    public TemplateInstance searchArtists(@QueryParam("q") String q) {
        List<ArtistSearchResult.ArtistDto> results = List.of();
        boolean tooShort = q == null || q.trim().length() < 2;
        if (!tooShort) {
            try {
                results = artistService.search(q.trim());
            } catch (Exception e) {
                results = List.of();
            }
        }
        List<String> followedMbids = FollowedArtist.<FollowedArtist>streamAll()
                .map(a -> a.mbid)
                .toList();
        return fragments_search_dropdown.data("results", results)
                .data("followedMbids", followedMbids)
                .data("query", q);
    }
}
