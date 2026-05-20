package com.app.api.repositories;
import org.springframework.data.repository.CrudRepository;

import com.app.api.models.HelperAnalytics;
//@Repository i have no clue why this is not working like its just red for fun 
public interface HelperAnalyticsRepository extends CrudRepository<HelperAnalytics, Integer> {
    
}