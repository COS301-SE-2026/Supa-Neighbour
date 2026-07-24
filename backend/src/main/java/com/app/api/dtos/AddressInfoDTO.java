package com.app.api.dtos;

/**
 * Returns the neighbourhood.
 *
 * @return the neighbourhood
 */
public class AddressInfoDTO {

    private int addressId;
    private int streetNumber;
    private String street;
    private int zipcode;
    private String fullAddress;

    /**
     * Creates an address info DTO.
     *
     * @param addressId    the address identifier
     * @param streetNumber the street number
     * @param street       the street name
     * @param zipcode      the postal code
     */
    public AddressInfoDTO(int addressId, int streesNumber, String street, int zipcode) {
        this.addressId = addressId;
        this.street = street;
        this.zipcode = zipcode;
        this.streetNumber = streesNumber;
    }

    /**
     * Builds the full address string.
     *
     * @return the full address
     */
    private String fullAddress() {
        String address = streetNumber + " " + street + "," + zipcode;
        return address;
    }

    /**
     * Returns the address identifier.
     *
     * @return the address identifier
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * Returns the street number.
     *
     * @return the street number
     */
    public int getStreetNumber() {
        return streetNumber;
    }

    /**
     * Returns the street name.
     *
     * @return the street name
     */
    public String getStreet() {
        return street;
    }

    /**
     * Returns the postal code.
     *
     * @return the postal code
     */
    public int getZipcode() {
        return zipcode;
    }

    /**
     * Returns the full address.
     *
     * @return the full address
     */
    public String getFullAddress() {
        String address = this.fullAddress();
        return address;
    }

    /**
     * Sets the address identifier.
     *
     * @param addressId the address identifier
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
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
     * Sets the street name.
     *
     * @param street the street name
     */
    public void setStreet(String street) {
        this.street = street;
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
     * Sets the full address.
     *
     * @param fullAddress the full address
     */
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
