package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    
} 
