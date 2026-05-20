package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "addresstable")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressid")
    private int addressid;
    @Column(name = "addressnumber")
    private int streetNumber;
    @Column(name = "addressstreet")
    private String street;
    @Column(name = "addresszipcode")
    private int zipcode;
    @Column(name = "neighbourhoodId`")
    private int neighbourhoodId;
    @Column(name = "residentid")
    private int residentid;

    public Address() {
    }

    public Address(int addressid, int streetNumber, String street, int zipcode, int neighbourhoodId, int residentid) {
        this.addressid = addressid;
        this.streetNumber = streetNumber;
        this.street = street;
        this.zipcode = zipcode;
        this.neighbourhoodId = neighbourhoodId;
        this.residentid = residentid;
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

    public int getNeighbourhoodId() {
        return neighbourhoodId;
    }

    public void setNeighbourhoodId(int neighbourhoodId) {
        this.neighbourhoodId = neighbourhoodId;
    }

    public int getResidentid() {
        return residentid;
    }

    public void setResidentid(int residentid) {
        this.residentid = residentid;
    }
    
}
