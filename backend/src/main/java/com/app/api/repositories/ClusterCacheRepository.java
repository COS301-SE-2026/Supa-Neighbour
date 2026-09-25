package com.app.api.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.EndorsementClusterCache;

public interface ClusterCacheRepository extends JpaRepository<EndorsementClusterCache, Integer> {
    
}
