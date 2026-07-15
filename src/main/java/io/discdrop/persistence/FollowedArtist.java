package io.discdrop.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table
public class FollowedArtist extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String mbid;

    @Column(nullable = false)
    public String name;

    public String sortName;

    public String disambiguation;

    public String areaName;

    public String type;

    public String country;

    @Column(nullable = false)
    public Instant followedAt;

    public Instant lastSyncedAt;

    public static FollowedArtist findByMbid(String mbid) {
        return find("mbid", mbid).firstResult();
    }

    public static boolean existsByMbid(String mbid) {
        return count("mbid", mbid) > 0;
    }
}
