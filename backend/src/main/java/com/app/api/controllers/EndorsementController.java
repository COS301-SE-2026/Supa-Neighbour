package com.app.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.services.EndorsementService;

@RestController
@RequestMapping("/api/endorsements")
public class EndorsementController {

    private final EndorsementService endorsementService;

    /**
     * Constructs a new {@code EndorsementController} with the given service.
     *
     * @param endorsementService the service used to handle endorsement operations
     */
    public EndorsementController(EndorsementService endorsementService) {
        this.endorsementService = endorsementService;
    }
}
