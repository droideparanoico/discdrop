package io.discdrop;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class StartupValidator {

    void onStart(@Observes StartupEvent event) {
        String ua = ConfigProvider.getConfig()
                .getOptionalValue("discdrop.mbz.user-agent", String.class)
                .orElse(null);
        if (ua == null || ua.isBlank()) {
            throw new IllegalStateException(
                "Missing required configuration 'discdrop.mbz.user-agent'. "
                + "MusicBrainz requires a meaningful User-Agent contact string.");
        }
    }
}
