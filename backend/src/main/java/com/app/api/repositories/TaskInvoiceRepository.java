package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.TaskInvoice;

/**
 * Repository for TaskInvoice entities.
 */
@Repository
public interface TaskInvoiceRepository extends JpaRepository<TaskInvoice, Integer> {
    

    
}
