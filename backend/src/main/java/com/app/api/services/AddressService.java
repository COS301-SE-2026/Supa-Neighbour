package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Address;
import com.app.api.repositories.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    // Get all
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    // Get by id
    public Address getAddressById(int id) {
        return addressRepository.findById(id).orElse(null);
    }

    // Create
    public Address saveAddress(Address address) {
        if(address == null) return null;
        return addressRepository.save(address);
    }

    // Update
    public Address updateAddress(int id, Address updated) {
        Address existing = addressRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setStreet(updated.getStreet());
        existing.setStreetNumber(updated.getStreetNumber());
        existing.setZipcode(updated.getZipcode());
        existing.setNeighbourhoodid(updated.getNeighbourhoodid());

        return addressRepository.save(existing);
    }

    // Delete
    public void deleteAddress(int id) {
        addressRepository.deleteById(id);
    }
}