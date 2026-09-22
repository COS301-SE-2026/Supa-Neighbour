package com.app.api.controllers;

import com.app.api.dtos.CreateEndorsementRequestDTO;
import com.app.api.dtos.EndorsementResponseDTO;
import com.app.api.dtos.EndorsementSummaryResponseDTO;
import com.app.api.dtos.TrustGraphResponseDTO.GraphDirection;
import com.app.api.dtos.MyEndorsementResponseDTO;
import com.app.api.dtos.SkillCatalogueResponseDTO;
import com.app.api.dtos.TrustGraphResponseDTO;
import com.app.api.dtos.TrustPathResponseDTO;
import com.app.api.models.User;
import com.app.api.services.EndorsementService;
 
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
 
import java.net.URI;

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
