package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Address;
import com.app.api.repositories.AddressRepository;

/**
 * Service layer for managing address operations.
 * Provides CRUD functionality for Address entities.
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    
    /**
     * Retrieves all addresses from the repository.
     *
     * @return a list of all addresses
     */
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

   /**
    * Retrieves an address by its identifier.
    *
    * @param id the address identifier
    * @return the address if found, or null if no address exists with the given id
    */
    public Address getAddressById(int id) {
        return addressRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new address to the repository.
     *
     * @param address the address to save
     * @return the saved address, or null if the provided address is null
     */
    public Address saveAddress(Address address) {
        if(address == null){ 
            return null;
        }
        return addressRepository.save(address);
    }

    /**
     * Updates an existing address with the provided details.
     *
     * @param id      the identifier of the address to update
     * @param updated the address object containing the updated fields
     * @return the updated address, or null if no address exists with the given id
     */
    public Address updateAddress(int id, Address updated) {
        Address existing = addressRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setStreet(updated.getStreet());
        existing.setStreetNumber(updated.getStreetNumber());
        existing.setZipcode(updated.getZipcode());
        existing.setNeighbourhoodid(updated.getNeighbourhoodid());

        return addressRepository.save(existing);
    }

    /**
     * Deletes an address by its identifier.
     *
     * @param id the identifier of the address to delete
     */
    public void deleteAddress(int id) {
        addressRepository.deleteById(id);
    }
}
