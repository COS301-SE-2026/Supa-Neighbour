package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "compatibilitytable")
public class Compatibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compatibilityid")
    private int id;

    @Column(name = "helperid")
    private int helperId;

    @Column(name = "dependentid")
    private int dependentId;

    @Column(name = "compatibilityscore")
    private int compatibilityScore;

    @Column(name = "compatibilitycolour")
    private String compatibilityColour;

    public Compatibility() {
    }

    public Compatibility(int helperId, int dependentId, int compatibilityScore, String compatibilityColour) {
        this.helperId = helperId;
        this.dependentId = dependentId;
        this.compatibilityScore = compatibilityScore;
        this.compatibilityColour = compatibilityColour;
    }

    public int getId() {
        return id;
    }

    public int getHelperId() {
        return helperId;
    }

    public int getDependentId() {
        return dependentId;
    }

    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHelperId(int helperId) {
        this.helperId = helperId;
    }

    public void setDependentId(int dependentId) {
        this.dependentId = dependentId;
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
