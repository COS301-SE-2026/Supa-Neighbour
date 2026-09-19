package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Endorsement;

public interface EndorsementRepository extends JpaRepository<Endorsement, Integer> {

} 
