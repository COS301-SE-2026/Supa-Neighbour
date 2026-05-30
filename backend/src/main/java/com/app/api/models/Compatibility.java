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

    public Compatibility() {
    }

    public Compatibility(int compatibilityid,Helper helperid, Dependent dependentid, int compatibilityScore, String compatibilityColour) {
        this.compatibilityid=compatibilityid;
        this.helperid = helperid;
        this.dependentid = dependentid;
        this.compatibilityScore = compatibilityScore;
        this.compatibilityColour = compatibilityColour;
    }

    public int getCompatibilityid() {
        return compatibilityid;
    }

    public Helper getHelperid() {
        return helperid;
    }

    public Dependent getDependentid() {
        return dependentid;
    }

    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompadibilityid(int compatibilityid) {
        this.compatibilityid = compatibilityid;
    }

    public void setHelperid(Helper helperid) {
        this.helperid = helperid;
    }

    public void setDependentid(Dependent dependentid) {
        this.dependentid = dependentid;
    }

    public void setCompatibilityScore(int compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public void setCompatibilityColour(String compatibilityColour) {
        this.compatibilityColour = compatibilityColour;
    }

    public String getCompatibilityColour() {
        return compatibilityColour;
    }
    
}
