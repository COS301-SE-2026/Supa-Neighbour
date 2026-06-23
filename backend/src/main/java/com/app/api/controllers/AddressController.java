package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.Address;
import com.app.api.services.AddressService;

/**
 * REST controller for managing addresses.
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // GET /api/addresses
    /**
     * Get all addresses.
     *
     * @return a list of all addresses
     */
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // GET /api/addresses/1
    /**
     * Get a single address by its ID.
     *
     * @param id the address ID
     * @return the matching address, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable int id) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }

    // POST /api/addresses
    /**
     * Create a new address.
     *
     * @param address the address to create
     * @return the saved address
     */
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address saved = addressService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/addresses/1
    /**
     * Update an existing address.
     *
     * @param id the ID of the address to update
     * @param address the updated address data
     * @return the updated address, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable int id, @RequestBody Address address) {
        Address existing = addressService.getAddressById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Address updated = addressService.updateAddress(id, address);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/addresses/1
    /**
     * Delete an address by its ID.
     *
     * @param id the ID of the address to delete
     * @return 204 No Content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable int id) {
        Address existing = addressService.getAddressById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
