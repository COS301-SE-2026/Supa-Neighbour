package com.app.api.repositories;

import com.app.api.models.EndorsementClusterCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClusterCacheRepository extends JpaRepository<EndorsementClusterCache, Long> {

    /**
     * Returns all cached cluster rows for a zone, ordered by cluster label
     * then user ID. Callers group by cluster label to reconstruct membership;
     * the stable order keeps that grouping reproducible.
     *
     * @param zoneId the zone to fetch
     * @return the zone's rows, or an empty list if none exist
     */
    List<EndorsementClusterCache> findByZoneIdOrderByClusterLabelAscUserIdAsc(int zoneId);

    /**
     * Deletes all cached cluster rows for a zone. Typically called by the
     * clustering job before writing a fresh snapshot.
     *
     * <p>Bulk delete — bypasses the persistence context, so it must run
     * inside a transaction and will leave any already-loaded entities stale.</p>
     *
     * @param zoneId the zone to clear
     * @return the number of rows deleted
     */
    @Modifying
    @Query("delete from EndorsementClusterCache c where c.zoneId = :zoneId")
    int deleteByZoneId(@Param("zoneId") int zoneId);

    /**
     * Returns the distinct zone IDs present in the cluster cache. Used by
     * abuse scanners that read pre-computed cluster data directly, without
     * depending on there being any fresh raw endorsement activity for the zone.
     *
     * @return the zone IDs with at least one cached cluster row
     */
    @Query("select distinct c.zoneId from EndorsementClusterCache c")
    List<Integer> findDistinctZoneIds();
}
