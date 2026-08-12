package com.app.api.controllers;

import com.app.api.dtos.HelperTaskResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperTasksService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST controller that provides endpoints for retrieving the
 * authenticated helper's task history.
 */
@RestController
@RequestMapping("/api/helpers")
public class HelperTaskController {
    private final HelperTasksService helperTasksService;
    private final FirebaseAuthService firebaseAuthService;


    /**
     * Constructs a {@code HelperTaskController} with the required services.
     *
     * @param helperTasksService service responsible for retrieving helper tasks
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public HelperTaskController(HelperTasksService helperTasksService, FirebaseAuthService firebaseAuthService){
        this.helperTasksService = helperTasksService;
        this.firebaseAuthService = firebaseAuthService;
    }

     /**
     * Retrieves the task history of the authenticated helper.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the helper's
     * tasks are retrieved. Results can be filtered by status and
     * paginated using the {@code limit} and {@code offset} parameters.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param statusFilter an optional task status used to filter the results
     * @param limit the maximum number of task records to return
     * @param offset the number of task records to skip for pagination
     * @return a {@link ResponseEntity} containing the helper's task history
     *         if the request is successful, or a 401 Unauthorized response
     *         if the Firebase token is invalid or expired
     */
    @GetMapping("/me/tasks")
    public ResponseEntity<?> getMyTasks(
        @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false)           String statusFilter,
            @RequestParam(defaultValue = "20")        int    limit,
            @RequestParam(defaultValue = "0")         int    offset
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            HelperTaskResponse response = helperTasksService.getAcceptedTasks(userId, statusFilter, limit, offset);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}
