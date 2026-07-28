package com.app.api.dtos;

import com.app.api.models.Location;

/**
 * Response DTO containing address information.
 */
public class AddressDTO {
    private int addressId;
    private Location neighbourhoodId;

    /**
     * Creates an address DTO.
     *
     * @param addressId       the address identifier
     * @param neighbourhoodId the neighbourhood location
     */
    public AddressDTO(int addressId, Location neighbourhoodId) {
        this.addressId = addressId;
        this.neighbourhoodId = neighbourhoodId;
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
     * Returns the neighbourhood.
     *
     * @return the neighbourhood
     */
    public Location getNeighbourhood() {
        return neighbourhoodId;
    }

}
