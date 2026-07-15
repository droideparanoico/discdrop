package io.discdrop.mbz;

import io.discdrop.mbz.dto.ArtistSearchResult;
import io.discdrop.mbz.dto.ReleaseGroupBrowseResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class MusicBrainzService {

    @Inject
    @RestClient
    MusicBrainzClient client;

    @ConfigProperty(name = "discdrop.mbz.rate-limit-ms", defaultValue = "1000")
    long rateLimitMs;

    private volatile long lastCallMillis = 0L;

    public synchronized void acquire() {
        long elapsed = System.currentTimeMillis() - lastCallMillis;
        if (elapsed < rateLimitMs) {
            try {
                Thread.sleep(rateLimitMs - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastCallMillis = System.currentTimeMillis();
    }

    public List<ArtistSearchResult.ArtistDto> searchArtists(String query, int limit) {
        acquire();
        ArtistSearchResult result = client.searchArtists(query, limit, "json");
        return result != null && result.artists != null ? result.artists : List.of();
    }

    public ReleaseGroupBrowseResult browseReleaseGroups(String mbid, String types, int limit, int offset) {
        acquire();
        return client.browseReleaseGroups(
                mbid, types, limit, offset, "artist-credits", "json", "website-default");
    }
}
