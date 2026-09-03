package com.app.api.dtos;
import lombok.Data;

/**
 * Raw address input from the registration form, before street parsing
 * or neighbourhood/location resolution.
 */
@Data
public class AddressInfoDTO {
    private String street;
    private int zip;
    private String town;

    /**
     * retuns the street
     * @return string street
     */
    public String getStreet(){
        return street;
    }

    /**
     * returns zipcode
     * @return zip
     */
    public int getZip(){
        return zip;
    }
  
    /**
     * returns the town
     * @return town
     */
    public String getTown(){
        return town;
    }
}

