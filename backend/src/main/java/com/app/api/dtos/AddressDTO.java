package com.app.api.dtos;

import com.app.api.models.Location;

/**
 * Response DTO containing address information.
 */
public class AddressDTO {
    private int addressId;
    private Location neighbourhoodId;

    public AddressDTO(int addressId,Location neighbourhoodId)
    {
        this.addressId= addressId;
        this.neighbourhoodId = neighbourhoodId;
    }

    public int getAddressId() {
        return addressId;
    }

    public Location getNeighbourhood() {
        return neighbourhoodId;
    }

    
}
