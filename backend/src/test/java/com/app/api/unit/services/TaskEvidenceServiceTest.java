package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.app.api.dtos.VerificationResultDTO;
import com.app.api.models.Task;
import com.app.api.models.TaskImage;
import com.app.api.models.TaskInvoice;
import com.app.api.models.TaskType;
import com.app.api.models.TaskVerification;
import com.app.api.repositories.TaskImageRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.repositories.TaskTypeRepository;
import com.app.api.repositories.TaskVerificationRepository;
import com.app.api.verification.ExifService;
import com.app.api.verification.GeoService;
import com.app.api.verification.HashService;
import com.app.api.verification.VerificationEngine;
import com.app.api.verification.VerificationProperties;
import com.app.api.verification.VerificationService;
import com.app.api.verification.VerificationService.ClientHints;
import com.app.api.verification.VerificationService.Evidence;
import com.app.api.verification.VerificationService.Outcome;
import com.app.api.verification.VerificationService.TaskFacts;
import com.app.api.services.BlobStorageService;
import com.app.api.services.TaskEvidenceService;

import com.app.api.vision.VisionResult;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests the glue only: VerificationService is mocked (its own behaviour is covered by
 * VerificationServiceTest), so these check what is gathered, what is stored, and what is skipped.
 */
@ExtendWith(MockitoExtension.class)
class TaskEvidenceServiceTest {

    private static final String REFERENCE_URL = "https://acct.blob.core.windows.net/task-images/ref.png";
    private static final String COMPLETION_URL = "https://acct.blob.core.windows.net/task-images/after.png";

    @Mock
    private TaskImageRepository taskImageRepository;
    @Mock
    private TaskVerificationRepository taskVerificationRepository;
    @Mock
    private TaskTypeRepository taskTypeRepository;
    @Mock
    private BlobStorageService blobStorageService;
    @Mock
    private VerificationService verificationService;
    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock 
    private TaskInvoiceRepository taskInvoiceRepository;

    private final HashService hashService = new HashService();
    private final VerificationProperties properties = new VerificationProperties();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private TaskEvidenceService service;

    @BeforeEach
    void setUp() {
        service = new TaskEvidenceService(taskImageRepository, taskVerificationRepository, taskTypeRepository, blobStorageService,
                verificationService, hashService, properties, objectMapper, transactionTemplate,taskInvoiceRepository);
    }

    // ---------------------------------------------------------------- happy path

    @Test
    void verifiedPhotoIsStoredAndLinkedToItsVerification() throws IOException {
        byte[] bytes = photo(1);
        MockMultipartFile file = png(bytes);
        Task task = task();
        stubReference(Optional.of(referenceRow()), new byte[] {9, 9});
        when(taskImageRepository.existsByImageHash(hashService.sha256Hex(bytes))).thenReturn(false);
        when(taskImageRepository.findCompletionPerceptualHashesExcludingTask(5)).thenReturn(List.of(123L));
        when(verificationService.evaluateCompletion(any(), any())).thenReturn(
                outcome(VerificationEngine.Status.VERIFIED, VisionResult.ok(List.of("tap"), 0.9, "Tap looks fixed", true)));
        when(blobStorageService.uploadTaskImage(file)).thenReturn(COMPLETION_URL);
        stubPersistence();

        ClientHints hints = new ClientHints("camera", OffsetDateTime.of(2026, 9, 20, 12, 0, 0, 0, ZoneOffset.ofHours(2)),
                -25.7, 28.2, 12.0, "device-1");
        VerificationResultDTO dto = service.submitCompletionEvidence(task, file, hints);

        // what the pipeline was given
        ArgumentCaptor<Evidence> evidence = ArgumentCaptor.forClass(Evidence.class);
        verify(verificationService).evaluateCompletion(any(), evidence.capture());
        assertArrayEquals(bytes, evidence.getValue().completionImage());
        assertArrayEquals(new byte[] {9, 9}, evidence.getValue().referenceImage());
        assertEquals(false, evidence.getValue().sha256AlreadyStored());
        assertEquals(List.of(123L), evidence.getValue().otherPerceptualHashes());

        // what was stored
        ArgumentCaptor<TaskImage> image = ArgumentCaptor.forClass(TaskImage.class);
        verify(taskImageRepository).save(image.capture());
        TaskImage saved = image.getValue();
        assertEquals("COMPLETION", saved.getImageType());
        assertEquals(COMPLETION_URL, saved.getImageUrl());
        assertEquals("sha-of-photo", saved.getImageHash());
        assertEquals(Long.valueOf(777L), saved.getPerceptualHash());
        assertEquals("CAMERA", saved.getCaptureSource());                       // normalised
        assertEquals(LocalDateTime.of(2026, 9, 20, 10, 0, 0), saved.getClientCapturedAt()); // stored as UTC
        assertEquals(Double.valueOf(-25.7), saved.getClientLat());
        assertEquals("[\"tap\"]", saved.getAiLabels());
        assertEquals("Tap looks fixed", saved.getAiInsight());
        assertEquals(Boolean.TRUE, saved.getCaptureTimeValid());

        ArgumentCaptor<TaskVerification> verification = ArgumentCaptor.forClass(TaskVerification.class);
        verify(taskVerificationRepository).save(verification.capture());
        assertEquals(TaskVerification.VerificationStatus.VERIFIED, verification.getValue().getStatus());
        assertEquals("[]", verification.getValue().getReasons());
        assertEquals(7, verification.getValue().getCompletionImage().getTaskImageId());

        // what came back
        assertEquals(11, dto.getVerificationId());
        assertEquals(5, dto.getTaskId());
        assertEquals("VERIFIED", dto.getStatus());
        assertEquals(Integer.valueOf(7), dto.getCompletionImageId());
        assertEquals(Boolean.TRUE, dto.getLocationVerified());
        assertEquals("Tap looks fixed", dto.getAiInsight());
    }

    // ---------------------------------------------------------------- exact duplicate

    @Test
    void exactDuplicateIsNotStoredButItsAttemptIsRecorded() throws IOException {
        MockMultipartFile file = png(photo(1));
        when(taskImageRepository.existsByImageHash(anyString())).thenReturn(true);
        when(verificationService.evaluateCompletion(any(), any())).thenReturn(
                outcome(VerificationEngine.Status.FAILED, VisionResult.unavailable(VisionResult.Outcome.ERROR, "skipped")));
        stubPersistence();

        VerificationResultDTO dto = service.submitCompletionEvidence(task(), file, null);

        verify(blobStorageService, never()).uploadTaskImage(any());
        verify(taskImageRepository, never()).save(any());
        verify(taskImageRepository, never()).findCompletionPerceptualHashesExcludingTask(anyInt());
        verify(taskImageRepository, never()).findFirstByTaskid_TaskidAndImageTypeOrderByUploadedAtAsc(anyInt(), anyString());

        ArgumentCaptor<Evidence> evidence = ArgumentCaptor.forClass(Evidence.class);
        verify(verificationService).evaluateCompletion(any(), evidence.capture());
        assertEquals(true, evidence.getValue().sha256AlreadyStored());
        assertNull(evidence.getValue().referenceImage());

        assertEquals("FAILED", dto.getStatus());
        assertNull(dto.getCompletionImageId());
    }

    // ---------------------------------------------------------------- reference photo

    @Test
    void taskWithoutAReferencePhotoStillGetsVerified() throws IOException {
        MockMultipartFile file = png(photo(1));
        stubReference(Optional.empty(), null);
        when(taskImageRepository.existsByImageHash(anyString())).thenReturn(false);
        when(taskImageRepository.findCompletionPerceptualHashesExcludingTask(5)).thenReturn(List.of());
        when(verificationService.evaluateCompletion(any(), any())).thenReturn(
                outcome(VerificationEngine.Status.NEEDS_REVIEW, VisionResult.unavailable(VisionResult.Outcome.ERROR, "no reference")));
        when(blobStorageService.uploadTaskImage(file)).thenReturn(COMPLETION_URL);
        stubPersistence();

        VerificationResultDTO dto = service.submitCompletionEvidence(task(), file, null);

        verify(blobStorageService, never()).downloadTaskImage(anyString());
        ArgumentCaptor<Evidence> evidence = ArgumentCaptor.forClass(Evidence.class);
        verify(verificationService).evaluateCompletion(any(), evidence.capture());
        assertNull(evidence.getValue().referenceImage());
        assertEquals("NEEDS_REVIEW", dto.getStatus());
    }

    @Test
    void unreadableReferencePhotoIsTreatedAsMissingAndAiFieldsStayEmpty() throws IOException {
        MockMultipartFile file = png(photo(1));
        stubReference(Optional.of(referenceRow()), null);
        when(blobStorageService.downloadTaskImage(REFERENCE_URL)).thenThrow(new IllegalArgumentException("URL is not a task image"));
        when(taskImageRepository.existsByImageHash(anyString())).thenReturn(false);
        when(taskImageRepository.findCompletionPerceptualHashesExcludingTask(5)).thenReturn(List.of());
        when(verificationService.evaluateCompletion(any(), any())).thenReturn(
                outcome(VerificationEngine.Status.NEEDS_REVIEW, VisionResult.unavailable(VisionResult.Outcome.CONTENT_FILTERED, "filter")));
        when(blobStorageService.uploadTaskImage(file)).thenReturn(COMPLETION_URL);
        stubPersistence();

        VerificationResultDTO dto = service.submitCompletionEvidence(task(), file, null);

        ArgumentCaptor<Evidence> evidence = ArgumentCaptor.forClass(Evidence.class);
        verify(verificationService).evaluateCompletion(any(), evidence.capture());
        assertNull(evidence.getValue().referenceImage());

        ArgumentCaptor<TaskImage> image = ArgumentCaptor.forClass(TaskImage.class);
        verify(taskImageRepository).save(image.capture());
        assertNull(image.getValue().getAiLabels());
        assertNull(image.getValue().getAiConfidence());
        assertNull(image.getValue().getAiInsight());
        assertNull(image.getValue().getCaptureSource());     // null hints: nothing reported
        assertNull(dto.getAiInsight());
    }

    // ---------------------------------------------------------------- what the pipeline is told about the task

    @Test
    void taskFactsCarryTheTypeDescriptionCoordinatesAndSchedule() throws IOException {
        MockMultipartFile file = png(photo(1));
        Task task = task();
        task.setTaskTypeId(3);
        task.setTaskLat(-25.7545);
        task.setTaskLng(28.2314);
        task.setStartDate(java.sql.Date.valueOf("2026-09-20"));
        task.setStartTime(LocalTime.of(9, 0));
        task.setEndDate(java.sql.Date.valueOf("2026-09-21"));
        task.setInstructions("Left tap drips");
        TaskType type = mock(TaskType.class);
        when(type.getDescription()).thenReturn("Home Repair");
        when(taskTypeRepository.findById(3)).thenReturn(Optional.of(type));
        stubReference(Optional.empty(), null);
        when(taskImageRepository.existsByImageHash(anyString())).thenReturn(false);
        when(taskImageRepository.findCompletionPerceptualHashesExcludingTask(5)).thenReturn(List.of());
        when(verificationService.evaluateCompletion(any(), any())).thenReturn(
                outcome(VerificationEngine.Status.NEEDS_REVIEW, VisionResult.unavailable(VisionResult.Outcome.ERROR, "x")));
        when(blobStorageService.uploadTaskImage(file)).thenReturn(COMPLETION_URL);
        stubPersistence();

        service.submitCompletionEvidence(task, file, null);

        ArgumentCaptor<TaskFacts> facts = ArgumentCaptor.forClass(TaskFacts.class);
        verify(verificationService).evaluateCompletion(facts.capture(), any());
        assertEquals("Home Repair", facts.getValue().taskType());
        assertEquals(Double.valueOf(-25.7545), facts.getValue().taskLat());
        assertEquals(Double.valueOf(28.2314), facts.getValue().taskLng());
        assertEquals(LocalDate.of(2026, 9, 20), facts.getValue().startDate());
        assertEquals(LocalTime.of(9, 0), facts.getValue().startTime());
        assertEquals(LocalDate.of(2026, 9, 21), facts.getValue().endDate());
        assertEquals("Fix leaking tap", facts.getValue().title());
        assertEquals("Left tap drips", facts.getValue().instructions());
    }

    @Test
    void taskWithoutTypeOrDatesStillMapsCleanly() throws IOException {
        MockMultipartFile file = png(photo(1));
        stubReference(Optional.empty(), null);
        when(taskImageRepository.existsByImageHash(anyString())).thenReturn(false);
        when(taskImageRepository.findCompletionPerceptualHashesExcludingTask(5)).thenReturn(List.of());
        when(verificationService.evaluateCompletion(any(), any())).thenReturn(
                outcome(VerificationEngine.Status.NEEDS_REVIEW, VisionResult.unavailable(VisionResult.Outcome.ERROR, "x")));
        when(blobStorageService.uploadTaskImage(file)).thenReturn(COMPLETION_URL);
        stubPersistence();

        service.submitCompletionEvidence(task(), file, null);

        ArgumentCaptor<TaskFacts> facts = ArgumentCaptor.forClass(TaskFacts.class);
        verify(verificationService).evaluateCompletion(facts.capture(), any());
        assertNull(facts.getValue().taskType());
        assertNull(facts.getValue().startDate());
        assertNull(facts.getValue().taskLat());
        verifyNoInteractions(taskTypeRepository);
    }

    // ---------------------------------------------------------------- bad files

    @Test
    void unsupportedFileTypeIsRejectedBeforeAnythingElseRuns() {
        MockMultipartFile gif = new MockMultipartFile("file", "a.gif", "image/gif", new byte[] {1, 2, 3});

        assertThrows(IllegalArgumentException.class, () -> service.submitCompletionEvidence(task(), gif, null));

        verifyNoInteractions(verificationService, blobStorageService, taskImageRepository, taskVerificationRepository);
    }

    @Test
    void emptyMissingAndOversizedFilesAreRejected() throws IOException {
        MockMultipartFile empty = new MockMultipartFile("file", "a.png", "image/png", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> service.submitCompletionEvidence(task(), empty, null));
        assertThrows(IllegalArgumentException.class, () -> service.submitCompletionEvidence(task(), null, null));

        properties.setMaxUploadBytes(10);
        MockMultipartFile big = png(photo(1));
        assertThrows(IllegalArgumentException.class, () -> service.submitCompletionEvidence(task(), big, null));

        verifyNoInteractions(verificationService, blobStorageService, taskImageRepository, taskVerificationRepository);
    }

    // ---------------------------------------------------------------- helpers

    private void stubReference(Optional<TaskImage> row, byte[] bytes) {
        when(taskImageRepository.findFirstByTaskid_TaskidAndImageTypeOrderByUploadedAtAsc(5, "REFERENCE")).thenReturn(row);
        if (row.isPresent() && bytes != null) {
            when(blobStorageService.downloadTaskImage(REFERENCE_URL)).thenReturn(bytes);
        }
    }

    /** Runs the transaction callback inline and hands out ids on save, like the database would. */
    private void stubPersistence() {
        when(transactionTemplate.execute(any()))
                .thenAnswer(inv -> ((TransactionCallback<?>) inv.getArgument(0)).doInTransaction(null));
        lenient().when(taskInvoiceRepository.findById(anyInt())).thenReturn(Optional.of(new TaskInvoice()));
        lenient().when(taskImageRepository.save(any(TaskImage.class))).thenAnswer(inv -> {
            TaskImage image = inv.getArgument(0);
            image.setTaskImageId(7);
            return image;
        });
        when(taskVerificationRepository.save(any(TaskVerification.class))).thenAnswer(inv -> {
            TaskVerification verification = inv.getArgument(0);
            verification.setVerificationId(11);
            return verification;
        });
    }

    private static Task task() {
        Task task = new Task();
        task.setTaskId(5);
        task.setTitle("Fix leaking tap");
        return task;
    }

    private static TaskImage referenceRow() {
        TaskImage row = new TaskImage();
        row.setImageUrl(REFERENCE_URL);
        return row;
    }

    private static Outcome outcome(VerificationEngine.Status status, VisionResult vision) {
        VerificationEngine.Result result = new VerificationEngine.Result(status, 0.9, 0.9, 1.0, 1.0, 1.0, List.of());
        ExifService.ExifData exif = new ExifService.ExifData(true, "Google", "Pixel 8", true, -25.7, 28.2,
                LocalDateTime.of(2026, 9, 20, 12, 0, 0));
        GeoService.GeoCheckResult geo = new GeoService.GeoCheckResult(true, true, 20.0, 75.0);
        return new Outcome(result, "sha-of-photo", 777L, exif, geo, true, true, vision);
    }

    private static MockMultipartFile png(byte[] bytes) {
        return new MockMultipartFile("file", "after.png", "image/png", bytes);
    }

    private static byte[] photo(long seed) throws IOException {
        Random rnd = new Random(seed);
        BufferedImage img = new BufferedImage(64, 48, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 48; y++) {
            for (int x = 0; x < 64; x++) {
                img.setRGB(x, y, rnd.nextInt(0xFFFFFF));
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }
}