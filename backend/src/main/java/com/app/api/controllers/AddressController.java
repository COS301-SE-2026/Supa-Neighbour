package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Address;
import com.app.api.services.AddressService;

/**
 * Address controller.
 */
@RestController
@RequestMapping("api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * Get all addresses.
     * @return addresses
     */
    @GetMapping
    public List<Address> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    /**
     * Get address by id.
     * @param id address id
     * @return address
     */
    @GetMapping("api/addresses/{id}")
    public Address getAddressById(@PathVariable int id) {
        return addressService.getAddressById(id);
    }

    /**
     * Create address.
     * @param address address
     * @return saved address
     */
    @PostMapping
    public Address createAddress(@RequestBody Address address) {
        return addressService.saveAddress(address);
    }
}