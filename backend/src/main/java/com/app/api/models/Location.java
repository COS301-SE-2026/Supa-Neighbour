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
@Table(name = "location_table")
public class Location {
    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id_seq")
    private int locationid;

    @Column(name = "location_centre_point")
    private int locationCentrePoint;

    @Column(name = "location_radius")
    private int locationRadius;

    @Column(name = "neighbourhood_id")
    private int neighbourhoodid;

    @Column(name = "neighbourhood_name")
    private String neighbourhoodName;

    public Location() {
    }

    public Location(int locationid,int locationRadius,int locationCentrePoint, int neighbourhoodid, String neighbourhoodName) {
        this.locationCentrePoint=locationCentrePoint;
        this.locationid=locationid;
        this.locationRadius = locationRadius;
        this.neighbourhoodid = neighbourhoodid;
        this.neighbourhoodName = neighbourhoodName;
    }
    
     public int getLocationid() {
        return locationid;
    }

    public int getLocationCentrePoint() {
        return locationCentrePoint;
    }

    public void setLocationcentrepoint(int locationCentrePoint) {
        this.locationCentrePoint = locationCentrePoint;
    }

    public int getLocationRadius() {
        return locationRadius;
    }

    public void setLocationRadius(int locationRadius) {
        this.locationRadius = locationRadius;
    }

    public int getNeighbourhoodid() {
        return neighbourhoodid;
    }

    public void setNeighbourhoodid(int neighbourhoodid) {
        this.neighbourhoodid = neighbourhoodid;
    }

    public String getNeighbourhoodName() {
        return neighbourhoodName;
    }

    public void setNeighbourhoodName(String neighbourhoodName) {
        this.neighbourhoodName = neighbourhoodName;
    }

}
