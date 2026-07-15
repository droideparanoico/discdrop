package io.discdrop.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table
public class ReleaseGroupEntity extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String mbid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rg_artist"))
    public FollowedArtist artist;

    @Column(nullable = false)
    public String title;

    public LocalDate firstReleaseDate;

    public String firstReleaseDateRaw;

    public String primaryType;

    public String secondaryTypes;

    @Column(nullable = false)
    public String mbzUrl;

    public Instant discoveredAt;

    public static ReleaseGroupEntity findByMbid(String mbid) {
        return find("mbid", mbid).firstResult();
    }

    public static long deleteByArtist(FollowedArtist artist) {
        return delete("artist", artist);
    }
}
