package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.ReportImage;

public interface ReportImageRepository extends JpaRepository<ReportImage, Integer> {
    /**
     * Retrieves all images linked to the report with the given ID.
     *
     * <p>Traverses the {@code report} association on {@link ReportImage} and
     * matches on its {@code reportId} field.</p>
     *
     * @param reportId the ID of the report whose images should be fetched
     * @return a list of matching images; empty if none exist, never {@code null}
     */
    List<ReportImage> findByReport_ReportId(Integer reportId);
}
