package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Address;
import com.app.api.models.Location;
import java.util.Optional;

/**
 * Repository for Address entities.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {


    /**
     * Finds an address matching the specified street number, street name,
     * postal code, and neighbourhood.
     *
     * @param streetNumber the street number of the address
     * @param street the street name of the address
     * @param zipcode the postal code of the address
     * @param neighbourhoodid the neighbourhood associated with the address
     * @return an Optional containing the matching Address, if one exists
     */
    Optional<Address>findByStreetNumberAndStreetAndZipcodeAndNeighbourhoodid(
        int streetNumber, String street, int zipcode, Location neighbourhoodid
    );
    
} 
