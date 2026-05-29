package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "compatibilitytable")
public class Compatibility {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compatibility_id_seq")
    @Column(name = "compatibilityid")
    private int compatibilityid;

    @Column(name = "helperid")
    private Helper helperid;

    @Column(name = "dependentid")
    private Dependent dependentid;

    @Column(name = "compatibilityscore")
    private int compatibilityScore;

    @Column(name = "compatibilitycolour")
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

    public Dependent getDependentId() {
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
