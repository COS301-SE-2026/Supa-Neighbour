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
@Table(name = "address_table")
/**
 * Represents a physical address of a user in an neighbourhood
 */
public class Address {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int addressid;

    @Column(name = "address_number")
    private int streetNumber;

    @Column(name = "address_street")
    private String street;

    @Column(name = "address_zip")
    private int zipcode;

    @ManyToOne
    @JoinColumn(name = "neighbourhood_id")
    private Location neighbourhoodid;

    /**
     * Default constructor.
     */
    public Address() {
    }

    /**
     * Constructs an Address object.
     *
     * @param addressid the unique address identifier
     * @param streetNumber the street number
     * @param street the street name
     * @param zipcode the postal code
     * @param neighbourhoodid the neighbourhood associated with the address
     */
    public Address(int addressid, int streetNumber, String street, int zipcode, Location neighbourhoodid) {
        this.addressid = addressid;
        this.streetNumber = streetNumber;
        this.street = street;
        this.zipcode = zipcode;
        this.neighbourhoodid = neighbourhoodid;
    }

    /**
     * Gets the address identifier.
     *
     * @return the address identifier
     */
    public int getAddressid() {
        return addressid;
    }

     /**
     * Sets the address identifier.
     *
     * @param addressid the address identifier
     */
    public void setAddressid(int addressid) {
        this.addressid = addressid;
    }

    /**
     * Gets the street number.
     *
     * @return the street number
     */
    public int getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets the street number.
     *
     * @param streetNumber the street number
     */
    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Gets the street name.
     *
     * @return the street name
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street name.
     *
     * @param street the street name
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the postal code.
     *
     * @return the postal code
     */
    public int getZipcode() {
        return zipcode;
    }

    /**
     * Sets the postal code.
     *
     * @param zipcode the postal code
     */
    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * Gets the neighbourhood associated with the address.
     *
     * @return the neighbourhood
     */
    public Location getNeighbourhoodid() {
        return neighbourhoodid;
    }

    /**
     * Sets the neighbourhood associated with the address.
     *
     * @param neighbourhoodid the neighbourhood
     */
    public void setNeighbourhoodid(Location neighbourhoodid) {
        this.neighbourhoodid = neighbourhoodid;
    }
}
