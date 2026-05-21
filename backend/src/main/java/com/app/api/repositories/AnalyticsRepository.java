package com.app.api.repositories;

import com.app.api.models.Analytics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends CrudRepository<Analytics, Integer>
{
    // will be used to get analytical records linked to task  before deletion
    Iterable<Analytics> findByTaskId(int taskId);
}