package io.discdrop.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_artist_primary_type", columnNames = { "artist_id", "primary_type" }
))
public class ArtistTypeSetting extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_type_setting_artist"))
    public FollowedArtist artist;

    @Column(name = "primary_type", nullable = false)
    public String primaryType;

    @Column(nullable = false)
    public boolean enabled;

    public static long deleteByArtist(FollowedArtist artist) {
        return delete("artist", artist);
    }

    public static java.util.List<ArtistTypeSetting> findByArtist(FollowedArtist artist) {
        return list("artist", artist);
    }
}
