package com.app.api.dtos;

public class AddressInfoDTO {
    
    private int addressId;
    private int streetNumber;
    private String street;
    private int zipcode;
    private String fullAddress;

    public AddressInfoDTO(int addressId,int streesNumber,String street,int zipcode)
    {
        this.addressId=addressId;
        this.street = street;
        this.zipcode = zipcode;
        this.streetNumber = streesNumber;
    }


    private String fullAddress() {
        String address = streetNumber+" "+ street+","+zipcode;
        return address;
    }


    public int getAddressId() {
        return addressId;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public int getZipcode() {
        return zipcode;
    }

    public String getFullAddress() {
        String address = this.fullAddress(); 
        return address;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    
}


