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

    List<EndorsementClusterCache> findByZoneIdOrderByClusterLabelAscUserIdAsc(int zoneId);

    @Modifying
    @Query("delete from EndorsementClusterCache c where c.zoneId = :zoneId")
    int deleteByZoneId(@Param("zoneId") int zoneId);
}