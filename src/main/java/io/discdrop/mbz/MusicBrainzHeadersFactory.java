package io.discdrop.mbz;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
@Provider
public class MusicBrainzHeadersFactory implements ClientHeadersFactory {

    private static final String USER_AGENT_KEY = "discdrop.mbz.user-agent";

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incoming,
                                                 MultivaluedMap<String, String> outgoing) {
        String ua = ConfigProvider.getConfig()
                .getOptionalValue(USER_AGENT_KEY, String.class)
                .filter(s -> s != null && !s.isBlank())
                .orElse(null);
        if (ua == null) {
            throw new IllegalStateException(
                "Missing required configuration 'discdrop.mbz.user-agent'. "
                + "MusicBrainz requires a meaningful User-Agent contact string.");
        }
        outgoing.putSingle("User-Agent", ua);
        return outgoing;
    }
}
