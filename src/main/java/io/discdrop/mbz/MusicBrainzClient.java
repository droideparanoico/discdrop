package io.discdrop.mbz;

import io.discdrop.mbz.dto.ArtistSearchResult;
import io.discdrop.mbz.dto.ReleaseGroupBrowseResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "musicbrainz")
@RegisterClientHeaders(MusicBrainzHeadersFactory.class)
public interface MusicBrainzClient {

    @GET
    @Path("/artist")
    ArtistSearchResult searchArtists(@QueryParam("query") String q,
                                     @QueryParam("limit") int limit,
                                     @QueryParam("fmt") String fmt);

    @GET
    @Path("/release-group")
    ReleaseGroupBrowseResult browseReleaseGroups(@QueryParam("artist") String mbid,
                                                 @QueryParam("type") String types,
                                                 @QueryParam("limit") int limit,
                                                 @QueryParam("offset") int offset,
                                                 @QueryParam("inc") String inc,
                                                 @QueryParam("fmt") String fmt,
                                                 @QueryParam("release-group-status") String status);
}
