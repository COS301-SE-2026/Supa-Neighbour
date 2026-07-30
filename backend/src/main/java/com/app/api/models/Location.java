package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a geographical location or neighbourhood.
 * Defines service areas with center points and radius for task matching.
 */
@Data
@Builder
@Entity
@Table(name = "location_table")
public class Location {
    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int locationid;

    @Column(name = "location_center_point")
    private int locationCenterPoint;

    @Column(name = "location_radius")
    private int locationRadius;

    @Column(name = "neighbourhood_id")
    private int neighbourhoodid;

    @Column(name = "neighbourhood_name")
    private String neighbourhoodName;

    /**
     * Default constructor.
     */
    public Location() {
    }

    /**
     * Constructs a Location with all fields specified.
     *
     * @param locationid          the location identifier
     * @param locationRadius      the radius of the service area
     * @param locationCenterPoint the center point of the service area
     * @param neighbourhoodid     the neighbourhood identifier
     * @param neighbourhoodName   the name of the neighbourhood
     */
    public Location(int locationid,int locationRadius,int locationCenterPoint, int neighbourhoodid, String neighbourhoodName) {
        this.locationCenterPoint=locationCenterPoint;
        this.locationid=locationid;
        this.locationRadius = locationRadius;
        this.neighbourhoodid = neighbourhoodid;
        this.neighbourhoodName = neighbourhoodName;
    }

    /**
     * Gets the location identifier.
     *
     * @return the location identifier
     */
    public int getLocationid() {
        return locationid;
    }

    /**
     * Gets the center point of the service area.
     *
     * @return the location center point
     */
    public int getLocationCenterPoint() {
        return locationCenterPoint;
    }

    /**
     * Sets the center point of the service area.
     *
     * @param LocationCenterPoint
     */
    public void setLocationCenterPoint(int LocationCenterPoint) {
        this.locationCenterPoint = LocationCenterPoint;
    }


    /**
     * Gets the radius of the service area.
     *
     * @return the location radius
     */
    public int getLocationRadius() {
        return locationRadius;
    }

    /**
     * Sets the radius of the service area.
     *
     * @param locationRadius the location radius
     */
    public void setLocationRadius(int locationRadius) {
        this.locationRadius = locationRadius;
    }

    /**
     * Gets the neighbourhood identifier.
     *
     * @return the neighbourhood identifier
     */
    public int getNeighbourhoodid() {
        return neighbourhoodid;
    }

    /**
     * Sets the neighbourhood identifier.
     *
     * @param neighbourhoodid the neighbourhood identifier
     */
    public void setNeighbourhoodid(int neighbourhoodid) {
        this.neighbourhoodid = neighbourhoodid;
    }

    /**
     * Gets the name of the neighbourhood.
     *
     * @return the neighbourhood name
     */
    public String getNeighbourhoodName() {
        return neighbourhoodName;
    }

    /**
    * Sets the name of the neighbourhood.
    *
    * @param neighbourhoodName the neighbourhood name
    */
    public void setNeighbourhoodName(String neighbourhoodName) {
        this.neighbourhoodName = neighbourhoodName;
    }

}
