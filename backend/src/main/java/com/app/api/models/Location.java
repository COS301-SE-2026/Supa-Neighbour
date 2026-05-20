package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "locationtable")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "locationcentrepoint")
    private int locationcentrepoint;
    @Column(name = "locationradius")
    private int locationRadius;
    @Column(name = "neighbourhoodid")
    private int neighbourhoodId;
    @Column(name = "neighbourhoodname")
    private int neighbourhoodName;

    public Location() {
    }

    public Location(int locationRadius, int neighbourhoodId, int neighbourhoodName) {
        this.locationRadius = locationRadius;
        this.neighbourhoodId = neighbourhoodId;
        this.neighbourhoodName = neighbourhoodName;
    }
    
     public int getId() {
        return id;
    }

    public int getLocationcentrepoint() {
        return locationcentrepoint;
    }

    public void setLocationcentrepoint(int locationcentrepoint) {
        this.locationcentrepoint = locationcentrepoint;
    }

    public int getLocationRadius() {
        return locationRadius;
    }

    public void setLocationRadius(int locationRadius) {
        this.locationRadius = locationRadius;
    }

    public int getNeighbourhoodId() {
        return neighbourhoodId;
    }

    public void setNeighbourhoodId(int neighbourhoodId) {
        this.neighbourhoodId = neighbourhoodId;
    }

    public int getNeighbourhoodName() {
        return neighbourhoodName;
    }

    public void setNeighbourhoodName(int neighbourhoodName) {
        this.neighbourhoodName = neighbourhoodName;
    }

}
