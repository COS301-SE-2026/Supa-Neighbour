package com.app.api.controllers;

import java.util.List;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.app.api.models.TaskInvoice;
import com.app.api.models.TaskVerification;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.TaskInvoiceService;
import com.app.api.verification.VerificationService.ClientHints;
import com.google.firebase.auth.FirebaseAuthException;
import com.app.api.services.TaskEvidenceService;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.models.Task;
import com.app.api.models.TaskImage;
import com.app.api.dtos.VerificationResultDTO;
import com.app.api.repositories.TaskVerificationRepository;
import com.app.api.repositories.DependentRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * REST controller for task invoice.
 */
@RestController
@RequestMapping("/api/taskinvoices")
@Tag(name = "Task Invoices", description = "Operations for managing task invoices")
public class TaskInvoiceController {

    private final TaskInvoiceService taskInvoiceService;
    private final FirebaseAuthService firebaseAuthService;
    private final TaskEvidenceService taskEvidenceService;
    private final TaskRepository taskRepository;
    private final HelperRepository helperRepository;
    private final TaskVerificationRepository taskVerificationRepository;
    private final DependentRepository dependentRepository;
    private final ObjectMapper objectMapper;


    /**
     * Constructs the controller with its required service dependency.
     *
     * @param taskInvoiceService service providing analytics data for taskInvoice
     */
    public TaskInvoiceController(TaskInvoiceService taskInvoiceService, FirebaseAuthService firebaseAuthService, TaskEvidenceService taskEvidenceService, TaskRepository taskRepository, HelperRepository helperRepository, TaskVerificationRepository taskVerificationRepository, DependentRepository dependentRepository, ObjectMapper objectMapper) {
        this.taskInvoiceService = taskInvoiceService;
        this.firebaseAuthService = firebaseAuthService;
        this.taskEvidenceService = taskEvidenceService;
        this.taskRepository = taskRepository;
        this.helperRepository = helperRepository;
        this.taskVerificationRepository = taskVerificationRepository;
        this.dependentRepository = dependentRepository;
        this.objectMapper = objectMapper;
    }

    // GET /api/taskinvoices    
    /**
     * Retrieves all task invoice.
     *
     * @return a list of all task invoice
     */
    @GetMapping
    @Operation(
        summary = "Get all task invoices",
        description = "Retrieves a list of all task invoices",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved task invoices")
    public ResponseEntity<List<TaskInvoice>> getAllTaskInvoices() {
        return ResponseEntity.ok(taskInvoiceService.getAllTaskInvoices());
    }

    // GET /api/taskinvoices/1
    /**
     * Retrieves a task invoice by its ID.
     *
     * @param id the task invoice ID
     * @return the task invoice if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get task invoice by ID",
        description = "Retrieves a single task invoice by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task invoice found"),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content)
    })
    public ResponseEntity<TaskInvoice> getTaskInvoiceById(
        @Parameter(description = "ID of the task invoice to retrieve", example = "1")
        @PathVariable int id
    ) {
        TaskInvoice taskInvoice = taskInvoiceService.getTaskInvoiceById(id);
        if (taskInvoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskInvoice);
    }

    // POST /api/taskinvoices
    /**
     * Creates a new task invoice.
     *
     * @param location the task invoice to create
     * @return the created task invoice with HTTP 201 status
     */
    @PostMapping
    @Operation(
        summary = "Create a new task invoice",
        description = "Creates a new task invoice for the authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task invoice created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task invoice data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> createTaskInvoice(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @RequestBody(required = false) TaskInvoice taskInvoice
    ) {
        if(taskInvoice == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            TaskInvoice saved = taskInvoiceService.saveTaskInvoice(userId, taskInvoice);
            if (saved == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Adding this to handle the exception handling within tests
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

    // PUT /api/taskinvoices/1
    /**
     * Updates an existing task invoice.
     *
     * @param id the ID of the task invoice to update
     * @param likes the updated task invoice data
     * @return the updated task invoice if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a task invoice",
        description = "Updates an existing task invoice by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task invoice updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid task invoice data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> updateTaskInvoice(
        @Parameter(description = "ID of the task invoice to update", example = "1")
        @PathVariable int id,
        @RequestBody TaskInvoice taskInvoice,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);

            TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }
            TaskInvoice updated = taskInvoiceService.updateTaskInvoice(id, taskInvoice);
            return ResponseEntity.ok(updated);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    // POST /api/taskinvoices/{id}/images
    /**
     * Saves a list of image URLs as TaskImage records linked to the given task.
     *
     * @param id the task invoice ID
     * @param body request body containing a list of image URLs under the key "imageUrls"
     * @param authHeader the Firebase Bearer token
     * @return 201 Created with the count of images saved, 404 if task not found, 401 if token invalid
     */
    @PostMapping("/{id}/images")
    @Operation(
        summary = "Add images to a task invoice",
        description = "Saves a list of Azure Blob Storage image URLs as completion proof for a task",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Images saved successfully"),
        @ApiResponse(responseCode = "400", description = "No image URLs provided", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content)
    })
    public ResponseEntity<?> addImagesToTask(
        @Parameter(description = "ID of the task invoice", example = "1")
        @PathVariable int id,
        @RequestBody Map<String, List<String>> body,
        @RequestParam(name = "type", defaultValue = "COMPLETION") String type,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true)
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);

            List<String> imageUrls = body.get("imageUrls");
            if (imageUrls == null || imageUrls.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No image URLs provided"));
            }

            int saved = taskInvoiceService.addImagesToTask(id, imageUrls, type);
            if (saved == -1) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("saved", saved));
        } catch (com.google.firebase.auth.FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    // DELETE /api/taskinvoices/1
    /**
     * Deletes a task invoice by its ID.
     *
     * @param id the ID of the task invoice to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a task invoice",
        description = "Deletes a task invoice by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task invoice deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTaskInvoice(
        @Parameter(description = "ID of the task invoice to delete", example = "1")
        @PathVariable int id
    ) {
        TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        taskInvoiceService.deleteTaskInvoice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Submits a helper completion photo for AI and location verification.
     *
     * @param id the task invoice ID
     * @param file the uploaded completion image
     * @param captureSource the capture source reported by the client
     * @param capturedAt the client capture timestamp
     * @param lat the client latitude
     * @param lng the client longitude
     * @param accuracyM the GPS accuracy in metres
     * @param deviceId the client device identifier
     * @param authHeader the bearer token for the authenticated caller
     * @return the verification result response
     */
    @PostMapping(value = "/{id}/completion-evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Submit a completion photo for verification",
        description = "Verifies the assigned helper's completion photo (location, capture time, duplicates and an AI comparison with the reference photo) and stores it",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Photo verified and stored"),
        @ApiResponse(responseCode = "400", description = "Missing, oversized or unsupported image", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "403", description = "Caller is not the helper assigned to this task", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "Task is closed or the photo was already submitted", content = @Content)
    })
    public ResponseEntity<?> submitCompletionEvidence(
        @Parameter(description = "ID of the task", example = "1")
        @PathVariable int id,

        @Parameter(description = "The completion photo (JPEG or PNG)", required = true)
        @RequestParam("file") MultipartFile file,

        @Parameter(description = "CAMERA for a photo taken in-app", example = "CAMERA")
        @RequestParam(name = "captureSource", required = false) String captureSource,

        @Parameter(description = "When the photo was taken, ISO-8601 with UTC offset", example = "2026-09-20T12:00:00+02:00")
        @RequestParam(name = "capturedAt", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime capturedAt,

        @Parameter(description = "Helper latitude when the photo was taken")
        @RequestParam(name = "lat", required = false) Double lat,
        @Parameter(description = "Helper longitude when the photo was taken")
        @RequestParam(name = "lng", required = false) Double lng,

        @Parameter(description = "GPS accuracy in metres")
        @RequestParam(name = "accuracyM", required = false) Double accuracyM,
        @Parameter(description = "Identifier for the capturing device")
        @RequestParam(name = "deviceId", required = false) String deviceId,

        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ){
        int callerId;
        try{
            String token = authHeader.replace("Bearer ", "");
            callerId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Task task = taskRepository.findById(id).orElse(null);
        if(task == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }

        Helper helper = helperRepository.findByUserid_Userid(callerId).orElse(null);

        boolean assignHelper = helper != null && Objects.equals(task.getHelperId(), helper.getHelperid());


        if(!assignHelper){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not the helper assigned to this task"));
        }

        String status = task.getStatus();
        if("cancelled".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "This task is no longer accepting evidence"));
        }


        ClientHints hints = new ClientHints(captureSource, capturedAt, lat, lng, accuracyM, deviceId);

        try{
            VerificationResultDTO result = taskEvidenceService.submitCompletionEvidence(task, file, hints);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch(DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "This photot has already been submitted"));
        }catch(IOException e){
            return ResponseEntity.internalServerError().body(Map.of("error", "An expected error has occured. Please try again"));
        }

    }

    @GetMapping("/{id}/completion-evidence")
    @Operation(
        summary = "Get completion photo verification results",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    public ResponseEntity<?> getCompletionEvidence(
        @PathVariable int id,
        @RequestHeader("Authorization") String authHeader
    ) {
        int callerId;
        try {
            callerId = firebaseAuthService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }

        // Assigned helper
        Helper helper = helperRepository.findByUserid_Userid(callerId).orElse(null);
        boolean isAssignedHelper = helper != null
            && Objects.equals(task.getHelperId(), helper.getHelperid());

        // Requester (assumes a dependentRepository mirroring helperRepository)
        Dependent dependent = dependentRepository.findByUserId_Userid(callerId);
        boolean isRequester = dependent != null
            && Objects.equals(task.getDependentId(), dependent.getDependentId());

        if (!isAssignedHelper && !isRequester /* && !isAdmin(callerId) */) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You do not have access to this task"));
        }

        List<VerificationResultDTO> results = taskVerificationRepository
        .findByTask_TaskidOrderByCreatedAtAscVerificationIdAsc(id).stream()
        .map(this::toDto)
        .toList();
        return ResponseEntity.ok(results);
    }

    private VerificationResultDTO toDto(TaskVerification v) {
        TaskImage img = v.getCompletionImage();
        return new VerificationResultDTO(
            v.getVerificationId(),
            v.getTask().getTaskid(),                 // match your TaskInvoice getter name
            v.getStatus().name(),
            v.getScore(),
            v.getLocationVerified(),
            v.getDistanceM(),
            v.getGeofenceRadiusM(),
            parseReasons(v.getReasons()),
            img == null ? null : img.getAiInsight(),
            img == null ? null : img.getTaskImageId()
        );
    }

    private List<String> parseReasons(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
