package io.discdrop.service;

import io.discdrop.persistence.ReleaseGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class FeedService {

    @ConfigProperty(name = "discdrop.feed.page-size", defaultValue = "25")
    int pageSize;

    private static final String ORDER_HQL =
            "ORDER BY firstReleaseDate DESC NULLS LAST, discoveredAt DESC";

    private PanacheQuery<ReleaseGroupEntity> orderedQuery() {
        return ReleaseGroupEntity.find(ORDER_HQL);
    }

    @Transactional
    public List<ReleaseGroupEntity> feedPage(int offset) {
        if (offset < 0) {
            offset = 0;
        }
        return orderedQuery().range(offset, offset + pageSize - 1).list();
    }

    @Transactional
    public List<ReleaseGroupEntity> latest(int count) {
        return orderedQuery().range(0, Math.max(0, count - 1)).list();
    }

    public int pageSize() {
        return pageSize;
    }
}
