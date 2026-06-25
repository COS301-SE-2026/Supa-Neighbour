package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.TaskInvoice;
import com.app.api.models.Analytics;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Integer> {
    List<Analytics> findByTaskid(TaskInvoice taskid);    
}

