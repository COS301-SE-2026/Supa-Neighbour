package com.app.api.controllers;

import java.util.List;

import com.app.api.dtos.AbuseFlagResponseDTO;
import com.app.api.dtos.ClusterAnalysisRunResponseDTO;
import com.app.api.dtos.ClusterMembershipResponseDTO;
import com.app.api.dtos.CreateEndorsementRequestDTO;
import com.app.api.dtos.EndorsementResponseDTO;
import com.app.api.dtos.EndorsementSummaryResponseDTO;
import com.app.api.dtos.TrustGraphResponseDTO.GraphDirection;
import com.app.api.dtos.MyEndorsementResponseDTO;
import com.app.api.dtos.SkillCatalogueResponseDTO;
import com.app.api.dtos.TrustGraphResponseDTO;
import com.app.api.dtos.TrustPathResponseDTO;
import com.app.api.models.User;
import com.app.api.dtos.UpdateFlagStatusRequestDTO;
import com.app.api.services.EndorsementService;
import org.springframework.http.HttpStatus;

import com.app.api.services.EndorsementAbuseService;
import com.app.api.services.EndorsementClusterService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.web.bind.annotation.RequestHeader;
import com.app.api.repositories.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final EndorsementClusterService endorsementClusterService;
    private final int ZONE_GRAPH_MIN_ADMIN_LEVEL = 1;
    private final EndorsementAbuseService endorsementAbuseService;

     /**
     * Constructs a new {@code EndorsementController} with the given collaborators.
     *
     * @param endorsementService        core endorsement operations
     * @param firebaseAuthService       resolves the caller from a Firebase ID token
     * @param userRepository            loads the acting {@link User}
     * @param endorsementClusterService cluster-analysis operations
     * @param endorsementAbuseService   abuse-flag operations
     */
    public EndorsementController(EndorsementService endorsementService,FirebaseAuthService firebaseAuthService,UserRepository userRepository, EndorsementClusterService endorsementClusterService,  EndorsementAbuseService endorsementAbuseService) {
        this.endorsementService = endorsementService;
        this.userRepository= userRepository;
        this.firebaseAuthService = firebaseAuthService;
        this.endorsementClusterService=endorsementClusterService; 
        this.endorsementAbuseService = endorsementAbuseService;
    }

    /**
     * Endorses another user for a skill, optionally tied to a completed task.
     *
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @param request    the endorsement to record
     * @return 201 with the created endorsement; 401 if the token is invalid;
     *         404 if the caller has no user record
     */
    @PostMapping
    @Operation(summary = "Create an endorsement",
               description = "Records an endorsement from the caller to another user for a skill.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Endorsement created successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or missing Firebase token"),
        @ApiResponse(responseCode = "404", description = "Caller has no user record")
    })
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
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @param skillTag   optional skill filter
     * @return 200 with the grouped listing; 401 if the token is invalid;
     *         404 if the caller has no user record
     */
    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @return 200 with the summary; 401 if the token is invalid; 404 if the
     *         caller has no user record
     */
    @GetMapping(path = "/me/summary", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @param depth      hops to expand, clamped server-side
     * @param direction  which endorsement edges to follow
     * @param limit      node cap, clamped server-side
     * @return 200 with nodes and edges; 401 if the token is invalid; 404 if
     *         the caller has no user record
     */
    @GetMapping(path = "/me/graph", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrustGraphResponseDTO> myGraph(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "2") int depth,
            @RequestParam(defaultValue = "BOTH") GraphDirection direction,
            @RequestParam(defaultValue = "200") int limit) {
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
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @param toUserId   the target user
     * @param maxDepth   search limit, clamped server-side
     * @param maxPaths   result cap, clamped server-side
     * @return 200 with the paths (or a disconnected result); 401 if the
     *         token is invalid; 404 if the caller has no user record
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

    /**
     * Triggers a fresh cluster-analysis run for a zone. Admin only.
     *
     * @param zoneId     the zone to analyse
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @return 200 with the run result; 401 if the token is invalid; 403 if the
     *         caller is not an admin; 404 if the caller has no user record
     */
    @PostMapping(path = "zone/{zoneId}/cluster-analysis",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClusterAnalysisRunResponseDTO> triggerClusterAnalysis(
        @PathVariable Integer zoneId,
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }  
            endorsementService.requireAdmin(ZONE_GRAPH_MIN_ADMIN_LEVEL, user);
            return ResponseEntity.ok(endorsementClusterService.runForZone(zoneId));
        }catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Returns the cached cluster membership for a zone. Admin only.
     *
     * @param zoneId     the zone to read
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @return 200 with the cached memberships; 401 if the token is invalid;
     *         403 if the caller is not an admin; 404 if the caller has no user record
     */
    @GetMapping(path = "zone/{zoneId}/clusters", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClusterMembershipResponseDTO> clusters(
        @PathVariable Integer zoneId,
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }  
            endorsementService.requireAdmin(ZONE_GRAPH_MIN_ADMIN_LEVEL, user);
            return ResponseEntity.ok(endorsementClusterService.readCached(zoneId));
        }catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }


    /**
     * Lists abuse flags, optionally filtered by status. Admin only.
     *
     * @param status     optional status filter ({@code open}, {@code investigate}, {@code dismiss})
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @return 200 with the flags; 401 if the token is invalid; 403 if the
     *         caller is not an admin; 404 if the caller has no user record
     */
    @GetMapping(path = "/admin/flags", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AbuseFlagResponseDTO>> listFlags(
        @RequestParam(required = false) String status,
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            if(user == null){
                return ResponseEntity.notFound().build();
            }

            endorsementService.requireAdmin(ZONE_GRAPH_MIN_ADMIN_LEVEL, user);
            return ResponseEntity.ok(endorsementAbuseService.listFlags(status));
        }catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Updates the review status of an abuse flag. Admin only.
     *
     * @param flagId     the flag to update
     * @param request    the new status
     * @param authHeader the {@code Authorization: Bearer <token>} header
     * @return 200 with the updated flag; 400 if the status is invalid; 401 if
     *         the token is invalid; 403 if the caller is not an admin; 404 if
     *         the caller or flag is not found
     */
    @PatchMapping(path = "/admin/flags/{flagId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbuseFlagResponseDTO> updateFlagStatus(
    @PathVariable Long flagId,
    @RequestBody UpdateFlagStatusRequestDTO request,
    @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            if(user == null){
                return ResponseEntity.notFound().build();
            }
            endorsementService.requireAdmin(ZONE_GRAPH_MIN_ADMIN_LEVEL, user);
            return ResponseEntity.ok(endorsementAbuseService.updateStatus(flagId, request.status()));
        }catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).build();
        }
    }
}

