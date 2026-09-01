package com.app.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.api.models.Address;
import com.app.api.repositories.AddressRepository;
import com.app.api.dtos.AddressInfoDTO;
import com.app.api.models.Location;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.transaction.annotation.Transactional;
import com.app.api.repositories.LocationRepository;

/**
 * Service layer for managing address operations.
 * Provides CRUD functionality for Address entities.
 */
@Service
public class AddressService {


    private final AddressRepository addressRepository;
    private final LocationRepository locationRepository;
    private static final Pattern STREET_PATTERN = Pattern.compile("^(\\d+)\\s+(.+)$");

    /**
     * Constructs the repository with its required service dependency.
     *
     * @param addressRepository repository providing analytics data for address
     */
    public AddressService(AddressRepository addressRepository,LocationRepository locationRepository) {
        this.addressRepository = addressRepository;
        this.locationRepository = locationRepository;
    }
    // Get all
    
    /**
     * Retrieves all addresses from the repository.
     *
     * @return a list of all addresses
     */
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    // Get by id
   /**
    * Retrieves an address by its identifier.
    *
    * @param id the address identifier
    * @return the address if found, or null if no address exists with the given id
    */
    public Address getAddressById(int id) {
        return addressRepository.findById(id).orElse(null);
    }

    // Create
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

    // Update
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

    // Delete
    /**
     * Deletes an address by its identifier.
     *
     * @param id the identifier of the address to delete
     */
    public void deleteAddress(int id) {
        addressRepository.deleteById(id);
    }

    /**
     * Resolves an address from raw registration-form input, creating the
     * underlying Location (neighbourhood) and Address rows only if they
     * don't already exist.
     *
     * @param request raw street/town/zip from the registration form
     * @return the existing or newly created Address
     * @throws IllegalArgumentException if the street doesn't start with a number
     */
    @Transactional
    public Address resolveOrCreateAddress(AddressInfoDTO request){
        if(request == null){
            return null;
        }

        Matcher matcher = STREET_PATTERN.matcher(request.getStreet().trim());
        if(!matcher.matches()){
            throw new IllegalArgumentException("Street must start with a number, e.g '15 Bond Street");
        }
        int streetNumber = Integer.parseInt(matcher.group(1));
        String streetName = matcher.group(2).trim();
        String town = request.getTown().trim();

        Location location = locationRepository.findByNeighbourhoodName(town)
        .orElseGet(() ->{
            Location newLocation = new Location();
            newLocation.setNeighbourhoodName(town);
            newLocation.setLocationCenterPoint(400);
            newLocation.setNeighbourhoodid(locationRepository.findMaxNeighbourhoodid() + 1);
            newLocation.setLocationRadius(20);
            return locationRepository.save(newLocation);
        });

        return addressRepository.findByStreetNumberAndStreetAndZipcodeAndNeighbourhoodid(streetNumber, streetName, request.getZip(), location)
        .orElseGet(() ->{
            Address newAddress = new Address();
            newAddress.setStreetNumber(streetNumber);
            newAddress.setStreet(streetName);
            newAddress.setZipcode(request.getZip());
            newAddress.setNeighbourhoodid(location);
            return addressRepository.save(newAddress);
        });
    }

}
