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

    public Address() {
    }

    public Address(int addressid, int streetNumber, String street, int zipcode, Location neighbourhoodid) {
        this.addressid = addressid;
        this.streetNumber = streetNumber;
        this.street = street;
        this.zipcode = zipcode;
        this.neighbourhoodid = neighbourhoodid;
    }

    public int getAddressid() {
        return addressid;
    }

    public void setAddressid(int addressid) {
        this.addressid = addressid;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public Location getNeighbourhoodid() {
        return neighbourhoodid;
    }

    public void setNeighbourhoodid(Location neighbourhoodid) {
        this.neighbourhoodid = neighbourhoodid;
    }
}