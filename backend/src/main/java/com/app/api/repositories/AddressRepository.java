package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Address;
import com.app.api.models.Location;
import java.util.Optional;

/**
 * Repository for Address entities.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address>findByStreetNumberAndStreetAndZipcodeAndNeighbourhoodid(
        int streetNumber, String street, int zipcode, Location neighbourhoodid
    );
    
} 
