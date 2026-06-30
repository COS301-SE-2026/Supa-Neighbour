package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

/**
 * Represents compatibility matching between helpers and dependents.
 * Stores compatibility scores and color-coded matching information.
 */
@Data
@Builder
@Entity
@Table(name = "compatibility_table")
public class Compatibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compatibility_id")
    private int compatibilityid;

    @ManyToOne
    @JoinColumn(name = "helper_id")
    private Helper helperid;

    @ManyToOne
    @JoinColumn(name = "dependent_id")
    private Dependent dependentid;

    @Column(name = "compatibility_score")
    private int compatibilityScore;

    @Column(name = "compatibility_colour")
    private String compatibilityColour;

    /**
     * Default constructor.
     */
    public Compatibility() {
    }

    /**
     * Constructs a Compatibility record with all fields specified.
     *
     * @param compatibilityid     the compatibility identifier
     * @param helperid            the helper associated with this compatibility record
     * @param dependentid         the dependent associated with this compatibility record
     * @param compatibilityScore  the compatibility score between the helper and dependent
     * @param compatibilityColour the colour code representing the compatibility match
     */
    public Compatibility(int compatibilityid,Helper helperid, Dependent dependentid, int compatibilityScore, String compatibilityColour) {
        this.compatibilityid=compatibilityid;
        this.helperid = helperid;
        this.dependentid = dependentid;
        this.compatibilityScore = compatibilityScore;
        this.compatibilityColour = compatibilityColour;
    }

    /**
     * Gets the compatibility identifier.
     *
     * @return the compatibility identifier
     */
    public int getCompatibilityid() {
        return compatibilityid;
    }

    /**
     * Gets the helper associated with this compatibility record.
     *
     * @return the helper
     */
    public Helper getHelperid() {
        return helperid;
    }

    /**
     * Gets the dependent associated with this compatibility record.
     *
     * @return the dependent
     */
    public Dependent getDependentid() {
        return dependentid;
    }

    /**
     * Gets the compatibility score between the helper and dependent.
     *
     * @return the compatibility score
     */
    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    /**
     * Sets the compatibility identifier.
     *
     * @param compatibilityid the compatibility identifier
     */
    public void setCompadibilityid(int compatibilityid) {
        this.compatibilityid = compatibilityid;
    }

    /**
     * Sets the helper associated with this compatibility record.
     *
     * @param helperid the helper
     */
    public void setHelperid(Helper helperid) {
        this.helperid = helperid;
    }

    /**
     * Sets the dependent associated with this compatibility record.
     *
     * @param dependentid the dependent
     */
    public void setDependentid(Dependent dependentid) {
        this.dependentid = dependentid;
    }

    /**
     * Sets the compatibility score between the helper and dependent.
     *
     * @param compatibilityScore the compatibility score
     */
    public void setCompatibilityScore(int compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    /**
     * Sets the colour code representing the compatibility match.
     *
     * @param compatibilityColour the compatibility colour
     */
    public void setCompatibilityColour(String compatibilityColour) {
        this.compatibilityColour = compatibilityColour;
    }

    /**
     * Gets the colour code representing the compatibility match.
     *
     * @return the compatibility colour
     */
    public String getCompatibilityColour() {
        return compatibilityColour;
    }

}
