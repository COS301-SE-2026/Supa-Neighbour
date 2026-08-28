package com.app.api.controllers;

import java.util.List;

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
import com.app.api.dtos.AddressInfoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing addresses.
 */
@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Addresses", description = "Operations for managing addresses")
public class AddressController {

    private final AddressService addressService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param addressService service providing analytics data for address.
     */
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Retrieves a list of all addresses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses")
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Retrieves a single address by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Address found"),
        @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<Address> getAddressById(
        @Parameter(description = "ID of the address to retrieve", example = "1")
        @PathVariable int id
    ) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }

    @PostMapping
    @Operation(summary = "Create a new address", description = "Creates a new address or resolves an existing one")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Address created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<?> createAddress(
        @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Address information to create or resolve",
            required = true
        ) AddressInfoDTO request
    ) {
        try{
            Address saved = addressService.resolveOrCreateAddress(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Updates an existing address by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Address updated successfully"),
        @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<Address> updateAddress(
        @Parameter(description = "ID of the address to update", example = "1")
        @PathVariable int id,
        @RequestBody Address address
    ) {
        Address existing = addressService.getAddressById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Address updated = addressService.updateAddress(id, address);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Deletes an address by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Address deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<Void> deleteAddress(
        @Parameter(description = "ID of the address to delete", example = "1")
        @PathVariable int id
    ) {
        Address existing = addressService.getAddressById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}