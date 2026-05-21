package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Address;
import com.app.api.repositories.AddressRepository;

/**
 * Address service.
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Get all addresses.
     * @return list of addresses
     */
    public Iterable<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    /**
     * Get address by id.
     * @param id address id
     * @return address
     */
    public Address getAddressById(int id) {
        return addressRepository.findById(id).orElse(null);
    }

    /**
     * Save address.
     * @param address address
     * @return saved address
     */
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }
}
