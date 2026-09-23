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
import org.springframework.http.HttpStatus;

import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.web.bind.annotation.RequestHeader;
import com.app.api.repositories.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Peer-endorsement endpoints.
 *
 * <p>Every authenticated route resolves the acting user from the Firebase ID
 * token. No route accepts a caller identity from the body or the query string.</p>
 */
@RestController
@RequestMapping("/api/endorsements")
@Tag(name = "Endorsements", description = "graphing the individuals external view of their work")
public class EndorsementController {

    private final EndorsementService endorsementService;
    private final FirebaseAuthService firebaseAuthService;
    private final UserRepository userRepository;
    private final int ZONE_GRAPH_MIN_ADMIN_LEVEL = 1;

    /**
     * Constructs a new {@code EndorsementController} with the given service.
     *
     * @param endorsementService the service used to handle endorsement operations
     */
    public EndorsementController(EndorsementService endorsementService,FirebaseAuthService firebaseAuthService,UserRepository userRepository) {
        this.endorsementService = endorsementService;
        this.userRepository= userRepository;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Endorses another user for a skill, optionally tied to a completed task.
     *
     * @param request the endorsement to record
     * @return 201 with the created endorsement and a {@code Location} header
     */
    @PostMapping
    @Operation(summary = "Get all endorsements", description = "retrieves everything")
    @ApiResponse(responseCode = "201", description =" created successfully")
    public ResponseEntity<EndorsementResponseDTO> create(@RequestHeader("Authorization") String authHeader,@Valid @RequestBody CreateEndorsementRequestDTO request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User endorser = userRepository.findById(userId)
                .orElse(null);
            
            if(endorser == null) {
                return ResponseEntity.notFound().build();
            }

            EndorsementResponseDTO created = endorsementService.create(endorser, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch(FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Lists the endorsements the caller has received, grouped by skill tag.
     *
     * @return 200 with the grouped listing
     */
    @GetMapping(path = "/me",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyEndorsementResponseDTO> myEndorsements(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false)String skillTag) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);

            User user = userRepository.findById(userId)
            .orElse(null);

            if(user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(endorsementService.listReceived(user, skillTag));
        } catch(FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Returns a compact endorsement summary sized for profile cards.
     *
     * @return 200 with the summary
     */
    @GetMapping(path = "/me/summary", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EndorsementSummaryResponseDTO>mySummary(@RequestHeader ("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId= firebaseAuthService.getUserIdFromToken(token);
            User user= userRepository.findById(userId)
                .orElse(null);

            if(user == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(endorsementService.summarise(user));
        } catch(FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Returns the caller's N-hop trust-graph neighbourhood.
     *
     * @param depth     hops to expand, clamped server-side
     * @param direction which endorsement edges to follow
     * @param limit     node cap, clamped server-side
     * @return 200 with nodes and edges
     */
    @GetMapping(path = "/me/graph",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrustGraphResponseDTO> myGraph(@RequestHeader("Authorization") String authHeader,
            @RequestParam (defaultValue = "2") int depth,
            @RequestParam(defaultValue =  "BOTH") GraphDirection direction,
            @RequestParam(defaultValue = "200")int limit) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user= userRepository.findById(userId)
                .orElse(null);

            if(user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(endorsementService.neighbourhood(user, depth, direction, limit));
        } catch(FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Returns the shortest trust path(s) from the caller to another user.
     *
     * @param toUserId the target user
     * @param maxDepth search limit, clamped server-side
     * @param maxPaths result cap, clamped server-side
     * @return 200 with the paths, or a disconnected result
     */
    @GetMapping(path = "/trust-paths", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrustPathResponseDTO> trustPaths(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Integer toUserId,
            @RequestParam(defaultValue = "4") int maxDepth,
            @RequestParam(defaultValue = "5") int maxPaths) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(endorsementService.trustPaths(user, toUserId, maxDepth, maxPaths));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Public catalogue of approved skill tags, grouped by category.
     *
     * <p>Mirrors the existing {@code /api/badges} pattern. If this should be
     * authenticated instead, add an explicit matcher in the security
     * configuration rather than changing this method.</p>
     *
     * @return 200 with the catalogue
     */
    @GetMapping(path = "/skills",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SkillCatalogueResponseDTO> skills() {
        return ResponseEntity.ok(endorsementService.catalogue());
    }

        /**
     * Returns the endorsement graph for a whole zone. Admin only.
     *
     * @param zoneId the zone to inspect
     * @param limit  edge cap, clamped server-side
     * @return 200 with the zone graph
     */

    @GetMapping (path = "/admin/zone/{zoneId}/graph", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrustGraphResponseDTO> zoneGraph(@PathVariable Integer zoneId,@RequestParam(defaultValue = "500")int limit,@RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            endorsementService.requireAdmin(ZONE_GRAPH_MIN_ADMIN_LEVEL, user);
            return ResponseEntity.ok(endorsementService.zoneGraph(zoneId, limit));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

}




