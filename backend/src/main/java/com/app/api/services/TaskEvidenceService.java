package com.app.api.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import com.app.api.models.TaskInvoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
 
import com.app.api.dtos.VerificationResultDTO;
import com.app.api.models.Task;
import com.app.api.models.TaskImage;
import com.app.api.models.TaskType;
import com.app.api.models.TaskVerification;
import com.app.api.repositories.TaskImageRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.repositories.TaskTypeRepository;
import com.app.api.repositories.TaskVerificationRepository;
import com.app.api.verification.HashService;
import com.app.api.verification.VerificationProperties;
import com.app.api.verification.VerificationService;
import com.app.api.verification.VerificationService.ClientHints;
import com.app.api.verification.VerificationService.Evidence;
import com.app.api.verification.VerificationService.Outcome;
import com.app.api.verification.VerificationService.TaskFacts;
import com.app.api.vision.VisionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service 
public class TaskEvidenceService {
    
    private static final Logger log = LoggerFactory.getLogger(TaskEvidenceService.class);

    static final String IMAGE_TYPE_REFERENCE = "REFERENCE";
    static final String IMAGE_TYPE_COMPLETION = "COMPLETION";
    
    private static final int CAPTURE_SOURCE_MAX = 20;
    private static final int DEVICE_ID_MAX = 100;

    private final TaskImageRepository taskImageRepository;
    private final TaskVerificationRepository taskVerificationRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final BlobStorageService blobStorageService;
    private final VerificationService verificationService;
    private final HashService hashService;
    private final VerificationProperties properties;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final TaskInvoiceRepository taskInvoiceRepository;


    /**
     * Constructs the service with its collaborators.
     *
     * @param taskImageRepository        repository for task images
     * @param taskVerificationRepository repository for verification results
     * @param taskTypeRepository         used to look up the task type's description for the AI prompt
     * @param blobStorageService         Azure Blob Storage access
     * @param verificationService        the verification pipeline
     * @param hashService                used to compute the SHA-256 for the duplicate check
     * @param properties                 verification settings (allowed types, size limit)
     * @param objectMapper               used to write the jsonb columns
     * @param transactionTemplate        used so both rows are saved atomically without holding a
     *                                   database connection open during the AI call
     */

    public TaskEvidenceService(
        TaskImageRepository taskImageRepository,
        TaskVerificationRepository taskVerificationRepository,
        TaskTypeRepository taskTypeRepository,
            BlobStorageService blobStorageService,
            VerificationService verificationService,
            HashService hashService,
            VerificationProperties properties,
            ObjectMapper objectMapper,
            TransactionTemplate transactionTemplate,
            TaskInvoiceRepository taskInvoiceRepository
    ){
        this.taskImageRepository = taskImageRepository;
        this.taskTypeRepository = taskTypeRepository;
        this.blobStorageService = blobStorageService;
        this.verificationService = verificationService;
        this.hashService = hashService;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.transactionTemplate = transactionTemplate;
        this.taskVerificationRepository = taskVerificationRepository;
        this.taskInvoiceRepository = taskInvoiceRepository;
    }

     /**
     * Verifies and stores a helper's completion photo.
     * <p>
     * An exact re-upload of a photo already in the system fails verification and is not stored again
     * (the unique index on image_hash would reject it); its attempt is still recorded as a
     * verification row with no image.
     * </p>
     *
     * @param task  the task the photo is for
     * @param file  the uploaded photo
     * @param hints what the helper's phone reported alongside the photo (may be null)
     * @return the verification result to return to the app
     * @throws IllegalArgumentException if the file is missing, too large, not an allowed type, or not a decodable image
     * @throws IOException              if the file cannot be read or stored
     */
    public VerificationResultDTO submitCompletionEvidence(Task task, MultipartFile file, ClientHints hints) throws IOException{

        validateFile(file);
        ClientHints clientHints = hints == null ? ClientHints.EMPTY : hints;
        byte[] photo = file.getBytes();

        int taskId = task.getTaskId();

        boolean sha256Stored = taskImageRepository.existsByImageHash(hashService.sha256Hex(photo));
        List<Long> otherHashes = sha256Stored ? List.of() : taskImageRepository.findCompletionPerceptualHashesExcludingTask(taskId);
        byte[] reference = sha256Stored ? null : loadReferencePhoto(taskId);


        Outcome outcome = verificationService.evaluateCompletion(toFacts(task), new Evidence(photo, reference, clientHints, sha256Stored, otherHashes));

        String imageUrl = sha256Stored ? null : blobStorageService.uploadTaskImage(file);
        TaskVerification saved = save(task, outcome, clientHints, imageUrl);

        return toDto(saved , outcome, taskId);
    }

    private void validateFile(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        boolean allowed = contentType != null && properties.getAllowedMime().stream().anyMatch(contentType:: equalsIgnoreCase);

        if(!allowed){
            throw new IllegalArgumentException("File must be a jpg or png");
        }

        if(file.getSize() >  properties.getMaxUploadBytes()){
            throw new IllegalArgumentException("File is too large");
        }
    }

    /**
     * The task's first REFERENCE photo, read back from blob storage. If there is none, or it cannot be
     * read, the pipeline simply runs without one (no AI comparison, so the result needs review).
     */
    private byte[] loadReferencePhoto(int taskId){
        Optional<TaskImage> reference = taskImageRepository.findFirstByTaskid_TaskidAndImageTypeOrderByUploadedAtAsc(taskId, IMAGE_TYPE_REFERENCE);

        if(reference.isEmpty()){
            return null;
        }

        try{
            return blobStorageService.downloadTaskImage(reference.get().getImageUrl());
        }catch(RuntimeException e){
            log.warn("Could not read reference photo for task {}: {}", taskId, e.getMessage());
            return null;
        }
    }

    private TaskFacts toFacts(Task task){
        String typeDescription = task.getTaskTypeId() == null ? null : taskTypeRepository.findById(task.getTaskTypeId()).map(TaskType::getDescription).orElse(null);
        return new TaskFacts(
            task.getTaskLat(),
            task.getTaskLng(),
            task.getStartDate().toLocalDate(),
            task.getStartTime(),
            task.getEndDate() == null ? null : task.getEndDate().toLocalDate(),
            task.getTitle(),
            task.getInstructions(),
            typeDescription
        );
    }

    private TaskVerification save(
        Task task, 
        Outcome outcome,
        ClientHints hints, 
        String imageUrl
    ){
        TaskImage image = imageUrl == null ? null : buildImage(task, outcome, hints, imageUrl);
        TaskVerification verification = buildVerification(task, outcome);
        return transactionTemplate.execute(
            status -> {
                TaskImage savedImage = image == null ? null : taskImageRepository.save(image);
                verification.setCompletionImage(savedImage);
                return  taskVerificationRepository.save(verification);
            }
        );
    }

    private TaskImage buildImage(
        Task task,
        Outcome outcome,
        ClientHints hints,
        String imageUrl
    ){
        TaskInvoice task1 = taskInvoiceRepository.findById(task.getTaskId()).orElseThrow(() -> new IllegalArgumentException("No TaskInvoice found for taskId " + task.getTaskId()));
        VisionResult vision = outcome.vision();
        return TaskImage.builder()
        .taskid(task1)
        .imageUrl(imageUrl)
        .imageType(IMAGE_TYPE_COMPLETION)
        .aiLabels(vision.available() ? toJson(vision.labels()) : null)
        .aiConfidence(vision.available() ? vision.confidence() : null)
        .aiInsight(vision.available() ? vision.insight() : null)
        .captureSource(limit(normalise(hints.captureSource()), CAPTURE_SOURCE_MAX))
        .clientCapturedAt(toUtcLocal(hints.clientCapturedAt()))
        .clientLat(hints.lat())
        .clientLng(hints.lng())
        .clientAccuracyM(hints.accuracyM())
        .deviceId(limit(hints.deviceId(), DEVICE_ID_MAX))
        .cameraMake(outcome.exif().cameraMake())
        .cameraModel(outcome.exif().cameraModel())
        .hasGps(outcome.exif().hasGps())
        .captureTimeValid(outcome.captureTimeValid())
        .imageHash(outcome.sha256())
        .perceptualHash(outcome.perceptualHash())
        .build();
    }

    private TaskVerification buildVerification(
        Task task,
        Outcome outcome
    ){
        TaskInvoice task1 = taskInvoiceRepository.findById(task.getTaskId()).orElseThrow(() -> new IllegalArgumentException("No TaskInvoice found for taskId " + task.getTaskId()));
        var result = outcome.result();
        var geo = outcome.geo();
        return TaskVerification.builder()
        .task(task1)
        .score(result.score())
        .status(TaskVerification.VerificationStatus.valueOf(result.status().name()))
        .locationVerified(geo.available() ? geo.locationVerified() : null)
        .distanceM(geo.distanceM())
        .geofenceRadiusM(geo.radiusM())
        .reasons(toJson(result.reasons()))
        .build();
    }

    private VerificationResultDTO toDto(TaskVerification saved, Outcome outcome, int taskId) {
        VisionResult vision = outcome.vision();
        Integer imageId = saved.getCompletionImage() == null ? null : saved.getCompletionImage().getTaskImageId();
        return new VerificationResultDTO(
                saved.getVerificationId(), taskId, saved.getStatus().name(), saved.getScore(),
                saved.getLocationVerified(), saved.getDistanceM(), saved.getGeofenceRadiusM(),
                outcome.result().reasons(),
                vision.available() ? vision.insight() : null,
                imageId);
    }

    private static LocalDateTime toUtcLocal(OffsetDateTime time) {
        return time == null ? null : time.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
 
    private static String normalise(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
 
    private static String limit(String value, int max) {
        return value != null && value.length() > max ? value.substring(0, max) : value;
    }

    private String toJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialise to JSON", e);
        }
    }
}
