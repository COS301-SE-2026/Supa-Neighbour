package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Address;

/**
 * Repository for Address entities.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {

    
} 
