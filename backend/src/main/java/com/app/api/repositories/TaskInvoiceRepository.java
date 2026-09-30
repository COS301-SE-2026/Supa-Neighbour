package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.api.models.TaskInvoice;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for TaskInvoice entity.
 */
@Repository
public interface TaskInvoiceRepository extends JpaRepository<TaskInvoice, Integer> {

    /**
     * Finds all TaskInvoice records associated with a specific helper id.
     * @param helperId the id of the helper
     */
    @Query("SELECT t FROM TaskInvoice t WHERE t.helperid.helperid = :helperId")
    List<TaskInvoice> findByHelperId(@Param("helperId") int helperId);

    /**
     * Counts the number of completed TaskInvoice records for a specific helper id.
     * @param helperId the id of the helper
     */
    @Query("SELECT COUNT(t) FROM TaskInvoice t WHERE t.helperid.helperid = :helperId AND t.status = 'completed'")
    long countCompletedByHelperId(@Param("helperId") int helperId);

    /**
     * Counts the total number of TaskInvoice records for a specific helper id.
     * @param helperId the id of the helper
     */
    @Query("SELECT COUNT(t) FROM TaskInvoice t WHERE t.helperid.helperid = :helperId")
    long countAllByHelperId(@Param("helperId") int helperId);

    /**
     * Counts the number of completed TaskInvoice records for a specific helper id since a given date.
     * @param helperId the id of the helper
     */
    @Query("SELECT COUNT(t) FROM TaskInvoice t WHERE t.helperid.helperid = :helperId AND t.status = 'completed' AND t.enddate >= :since")
    long countCompletedByHelperIdSince(@Param("helperId") int helperId, @Param("since") LocalDate since);

    /**
     * Finds the earliest start date of TaskInvoice records for a specific helper id.
     * @param helperId the id of the helper
     */
    @Query("SELECT MIN(t.startdate) FROM TaskInvoice t WHERE t.helperid.helperid = :helperId")
    LocalDate findEarliestStartDateByHelperId(@Param("helperId") int helperId);
}
