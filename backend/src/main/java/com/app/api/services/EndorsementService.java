package com.app.api.services;

import org.springframework.stereotype.Service;

import com.app.api.repositories.EndorsementRepository;

@Service
public class EndorsementService {

    private final EndorsementRepository endorsementRepository;

    /**
     * Constructs a new {@code EndorsementService} with the given repository.
     *
     * @param endorsementRepository the repository used for endorsement persistence
     */
    public EndorsementService(EndorsementRepository endorsementRepository) {
        this.endorsementRepository = endorsementRepository;
    }
} 
