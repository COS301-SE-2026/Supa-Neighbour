package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Address;
import com.app.api.services.AddressService;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // GET /api/addresses
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // GET /api/addresses/1
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable int id) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }

    // POST /api/addresses
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address saved = addressService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/addresses/1
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