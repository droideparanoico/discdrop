package io.discdrop.resource;

import io.discdrop.persistence.AppSetting;
import io.discdrop.service.SettingsService;
import io.discdrop.service.SyncService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;
import java.util.Set;

@Path("/settings")
public class SettingsResource {

    @Inject
    SettingsService settingsService;

    @Inject
    SyncService syncService;

    @Inject
    @Location("fragments/settings-panel.html")
    Template fragments_settings_panel;

    private static final List<Integer> SCHEDULE_OPTIONS = List.of(6, 12, 24);

    @GET
    public TemplateInstance panel() {
        return buildPanel(null);
    }

    @POST
    public TemplateInstance save(@FormParam("defaultPrimaryTypes") Set<String> defaultPrimaryTypes,
                                 @FormParam("syncScheduleHours") int syncScheduleHours,
                                 @FormParam("hideFutureFeed") boolean hideFutureFeed,
                                 @FormParam("hideFutureRss") boolean hideFutureRss) {
        if (defaultPrimaryTypes == null) {
            defaultPrimaryTypes = Set.of();
        }
        settingsService.setDefaultPrimaryTypes(defaultPrimaryTypes);
        int previous = settingsService.getSyncScheduleHours();
        settingsService.setSyncScheduleHours(syncScheduleHours);
        if (previous != syncScheduleHours) {
            syncService.reschedule();
        }
        settingsService.setHideFutureFeed(hideFutureFeed);
        settingsService.setHideFutureRss(hideFutureRss);
        return buildPanel("saved");
    }

    private TemplateInstance buildPanel(String flash) {
        return fragments_settings_panel
                .data("defaultPrimaryTypes", settingsService.getDefaultPrimaryTypes())
                .data("syncScheduleHours", settingsService.getSyncScheduleHours())
                .data("hideFutureFeed", settingsService.isHideFutureFeed())
                .data("hideFutureRss", settingsService.isHideFutureRss())
                .data("allPrimaryTypes", SettingsService.ALL_PRIMARY_TYPES)
                .data("scheduleOptions", SCHEDULE_OPTIONS)
                .data("flash", flash);
    }
}
