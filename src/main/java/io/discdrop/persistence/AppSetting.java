package io.discdrop.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class AppSetting extends PanacheEntity {

    @Column(name = "setting_key", nullable = false, unique = true)
    public String key;

    @Column(name = "setting_value", nullable = false, columnDefinition = "TEXT")
    public String value;

    public static AppSetting find(String key) {
        return find("key", key).firstResult();
    }

    public static String get(String key, String defaultValue) {
        AppSetting s = find(key);
        return s != null ? s.value : defaultValue;
    }

    public static void set(String key, String value) {
        AppSetting s = find(key);
        if (s == null) {
            s = new AppSetting();
            s.key = key;
            s.value = value;
            s.persist();
        } else {
            s.value = value;
        }
    }
}
