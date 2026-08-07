package com.app.api.dtos;
import lombok.Data;

/**
 * Raw address input from the registration form, before street parsing
 * or neighbourhood/location resolution.
 */
public class AddressInfoDTO {
    private String street;
    private int zip;
    private String town;

    public String getStreet(){
        return street;
    }

    public int getZip(){
        return zip;
    }
  
    public String getTown(){
        return town;
    }
}

