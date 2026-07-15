package io.discdrop.service;

import io.discdrop.persistence.AppSetting;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class SettingsService {

    public static final String KEY_DEFAULT_PRIMARY_TYPES = "defaultPrimaryTypes";
    public static final String KEY_SYNC_SCHEDULE_HOURS = "syncScheduleHours";

    public static final List<String> ALL_PRIMARY_TYPES = List.of("album", "single", "ep", "broadcast", "other");

    public Set<String> getDefaultPrimaryTypes() {
        String raw = AppSetting.get(KEY_DEFAULT_PRIMARY_TYPES, "album");
        return parseTypes(raw);
    }

    public void setDefaultPrimaryTypes(Set<String> types) {
        AppSetting.set(KEY_DEFAULT_PRIMARY_TYPES, String.join(",", types));
    }

    public int getSyncScheduleHours() {
        String raw = AppSetting.get(KEY_SYNC_SCHEDULE_HOURS, "24");
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 24;
        }
    }

    public void setSyncScheduleHours(int hours) {
        AppSetting.set(KEY_SYNC_SCHEDULE_HOURS, String.valueOf(hours));
    }

    private Set<String> parseTypes(String raw) {
        if (raw == null || raw.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
