package io.discdrop.resource;

import io.discdrop.persistence.ReleaseGroupEntity;
import io.discdrop.service.FeedService;
import io.discdrop.service.SettingsService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Path("/rss")
public class RssResource {

    private static final DateTimeFormatter RFC822 = DateTimeFormatter.RFC_1123_DATE_TIME;

    @Inject
    FeedService feedService;

    @Inject
    SettingsService settingsService;

    @Inject
    @Location("rss.xml")
    Template rss;

    @ConfigProperty(name = "discdrop.rss.item-count", defaultValue = "50")
    int itemCount;

    public record RssItem(String title, String link, String guid, String pubDate,
                          String description, String mediaUrl) {}

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public TemplateInstance feed() {
        boolean hideFuture = settingsService.isHideFutureRss();
        List<ReleaseGroupEntity> entities = feedService.latest(itemCount, hideFuture);
        List<RssItem> items = new ArrayList<>(entities.size());
        for (ReleaseGroupEntity e : entities) {
            String pubDate = null;
            if (e.firstReleaseDate != null) {
                pubDate = e.firstReleaseDate.atStartOfDay(ZoneOffset.UTC).format(RFC822);
            }
            StringBuilder desc = new StringBuilder();
            if (e.primaryType != null) {
                desc.append("Type: ").append(e.primaryType);
            }
            if (e.secondaryTypes != null) {
                desc.append(", ").append(e.secondaryTypes);
            }
            desc.append("\nReleased: ").append(e.firstReleaseDateRaw != null ? e.firstReleaseDateRaw : "unknown");
            items.add(new RssItem(
                    e.artist.name + " – " + e.title,
                    e.mbzUrl,
                    e.mbzUrl,
                    pubDate,
                    desc.toString(),
                    "https://coverartarchive.org/release-group/" + e.mbid + "/front"
            ));
        }
        return rss.data("items", items)
                .data("buildDate", OffsetDateTime.now(ZoneOffset.UTC).format(RFC822));
    }
}
