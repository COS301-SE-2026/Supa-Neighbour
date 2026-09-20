package com.app.api.verification;

import com.app.api.vision.VisionClient;
import com.app.api.vision.VisionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class VerificationService {

    /** What the helper's phone reports alongside the photo. Every field may be null. */
    public record ClientHints(String captureSource,
                              OffsetDateTime clientCapturedAt,
                              Double lat, Double lng, Double accuracyM,
                              String deviceId) {
        public static final ClientHints EMPTY = new ClientHints(null, null, null, null, null, null);
    }

    /**
     * The facts about the task the pipeline needs. Deliberately plain values, not a JPA entity: the
     * endpoint glue maps your task entity onto this, so this class doesn't depend on the entity's shape.
     */
    public record TaskFacts(Double taskLat, Double taskLng,
                            LocalDate startDate, LocalTime startTime, LocalDate endDate,
                            String title, String instructions, String taskType) { }

    /**
     * @param completionImage       the helper's photo, exactly as uploaded (original bytes)
     * @param referenceImage        the resident's reference photo, or null if this task has none
     * @param hints                 client-reported capture info (null is treated as "nothing reported")
     * @param sha256AlreadyStored   caller checked: does any stored image already have this file's SHA-256?
     *                              Compute it with HashService.sha256Hex and query BEFORE saving - the unique
     *                              index on image_hash would otherwise reject the insert with an exception.
     * @param otherPerceptualHashes perceptual hashes of other tasks' completion photos to compare against
     */
    public record Evidence(byte[] completionImage, byte[] referenceImage, ClientHints hints,
                           boolean sha256AlreadyStored, List<Long> otherPerceptualHashes) { }

    /**
     * Everything to persist / return. {@code vision} is never null: when the AI was skipped or
     * failed it is an "unavailable" result whose detail says why.
     */
    public record Outcome(VerificationEngine.Result result,
                          String sha256, long perceptualHash,
                          ExifService.ExifData exif,
                          GeoService.GeoCheckResult geo,
                          boolean captureTimeValid, boolean withinTaskWindow,
                          VisionResult vision) { }

    /** Result of analysing the resident's reference photo when it is uploaded. */
    public record ReferenceOutcome(long perceptualHash, VisionResult vision) { }

    private final ExifService exifService;
    private final GeoService geoService;
    private final HashService hashService;
    private final VisionClient visionClient;
    private final VerificationProperties properties;
    private final VerificationEngine engine;
    private final Clock clock;

    /**
     * Creates a service wired with production dependencies.
     *
     * @param exifService the service used to extract EXIF metadata from images
     * @param geoService  the service used to compute and verify geographic distance
     * @param hashService the service used to compute exact and perceptual image hashes
     * @param visionClient the client used to obtain AI vision signals for images
     * @param properties  configuration properties controlling weights, thresholds,
     *                    and other verification behaviour
     */
    @Autowired
    public VerificationService(ExifService exifService, GeoService geoService, HashService hashService,
                               VisionClient visionClient, VerificationProperties properties) {
        this(exifService, geoService, hashService, visionClient, properties, Clock.systemUTC());
    }

    /** For tests: lets them pin "now". */
    VerificationService(ExifService exifService, GeoService geoService, HashService hashService,
                        VisionClient visionClient, VerificationProperties properties, Clock clock) {
        this.exifService = exifService;
        this.geoService = geoService;
        this.hashService = hashService;
        this.visionClient = visionClient;
        this.properties = properties;
        this.engine = new VerificationEngine(properties.toEngineConfig());
        this.clock = clock;
    }

    // ---------------------------------------------------------------- completion photo

    /**
     * @throws IllegalArgumentException if the completion image is missing or not a decodable image
     *                                  (JPEG/PNG/GIF) - the caller should answer 400.
     */
    public Outcome evaluateCompletion(TaskFacts task, Evidence evidence) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        if (evidence == null){ 
            throw new IllegalArgumentException("evidence must not be null");
        }
        byte[] completion = evidence.completionImage();
        requireImage(completion, "completionImage");
        ClientHints hints = evidence.hints() == null ? ClientHints.EMPTY : evidence.hints();
        Instant now = clock.instant();

        // 1. Metadata from the ORIGINAL bytes (resizing would strip EXIF).
        ExifService.ExifData exif = exifService.extract(completion);

        // 2. Fingerprints. perceptualHash throws IllegalArgumentException for undecodable images.
        String sha256 = hashService.sha256Hex(completion);
        long perceptualHash = hashService.perceptualHash(completion);

        // 3. Duplicates.
        boolean exactReuse = evidence.sha256AlreadyStored();
        boolean nearDuplicate = false;
        if (evidence.otherPerceptualHashes() != null) {
            for (Long other : evidence.otherPerceptualHashes()) {
                if (other != null && hashService.isNearDuplicate(perceptualHash, other)) {
                    nearDuplicate = true;
                    break;
                }
            }
        }
        byte[] reference = usableReference(evidence.referenceImage());
        boolean matchesReference = false;
        if (reference != null) {
            try {
                matchesReference = hashService.isNearDuplicate(perceptualHash, hashService.perceptualHash(reference));
            } catch (IllegalArgumentException e) {
                reference = null; // an undecodable reference photo is as good as none
            }
        }

        // 4. Location, capture time, task window.
        GeoService.GeoCheckResult geo = geoService.check(
                hints.lat(), hints.lng(), hints.accuracyM(),
                task.taskLat(), task.taskLng(), properties.getGeofenceRadiusM());
        boolean captureTimeValid = isCaptureTimeValid(hints, exif, now);
        boolean withinTaskWindow = isWithinTaskWindow(task, now);

        // 5. Vision - skipped when it cannot matter (exact duplicate = hard fail) or cannot work (no reference).
        VisionResult vision;
        if (exactReuse) {
            vision = VisionResult.unavailable(VisionResult.Outcome.ERROR, "skipped: exact duplicate image");
        } else if (reference == null) {
            vision = VisionResult.unavailable(VisionResult.Outcome.ERROR, "no usable reference photo for this task");
        } else {
            vision = visionClient.compare(
                    new VisionClient.TaskContext(task.title(), task.instructions(), task.taskType()),
                    reference, completion);
        }

        // 6. Decide.
        VerificationEngine.Input.Builder input = VerificationEngine.Input.builder()
                .metadata(hints.captureSource(), captureTimeValid, exif.hasCameraInfo(), exif.hasGps())
                .withinTaskWindow(withinTaskWindow)
                .exactHashReused(exactReuse)
                .nearDuplicate(nearDuplicate)
                .matchesReference(matchesReference);
        if (vision.available()) {
            input.ai(vision.confidence(), vision.taskLooksComplete());
        }
        if (geo.available()) {
            input.geo(geo.distanceM(), geo.radiusM());
        }

        return new Outcome(engine.evaluate(input.build()), sha256, perceptualHash, exif, geo,
                captureTimeValid, withinTaskWindow, vision);
    }

    // ---------------------------------------------------------------- reference photo

    /**
     * Fingerprints and describes the resident's reference photo when it is attached to a task.
     *
     * @throws IllegalArgumentException if the image is missing or not a decodable image
     */
    public ReferenceOutcome analyzeReference(TaskFacts task, byte[] referenceImage) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        requireImage(referenceImage, "referenceImage");
        long perceptualHash = hashService.perceptualHash(referenceImage);
        VisionResult vision = visionClient.analyzeBaseline(
                new VisionClient.TaskContext(task.title(), task.instructions(), task.taskType()),
                referenceImage);
        return new ReferenceOutcome(perceptualHash, vision);
    }

    // ---------------------------------------------------------------- derived booleans

    /**
     * Was this a fresh capture, and does it hang together?
     *  - the client reported a capture time,
     *  - it is within capture-max-age-minutes of server time (the photo was just taken), and
     *  - if the file carries an EXIF capture time, it agrees with the client's within
     *    exif-tolerance-minutes. EXIF has no zone, so it is compared with the client's local
     *    wall-clock time; missing EXIF is not held against the helper.
     */
    boolean isCaptureTimeValid(ClientHints hints, ExifService.ExifData exif, Instant now) {
        OffsetDateTime reported = hints.clientCapturedAt();
        if (reported == null) {
            return false;
        }

        Duration age = Duration.between(reported.toInstant(), now).abs();
        if (age.compareTo(Duration.ofMinutes(properties.getCaptureMaxAgeMinutes())) > 0){ 
            return false;
        }

        LocalDateTime exifTime = exif.capturedAt();
        if (exifTime != null) {
            Duration gap = Duration.between(exifTime, reported.toLocalDateTime()).abs();
            return gap.compareTo(Duration.ofMinutes(properties.getExifToleranceMinutes())) <= 0;
        }
        return true;
    }

    /**
     * Is "now" (server time) inside the task's window? The window runs from start_date + start_time
     * (midnight if no time) to the end of end_date (or of start_date if there is no end date), widened by
     * task-window-grace-minutes on both sides, in the configured zone. A task with no start date has no
     * window, so this returns false (fail safe: it goes to review).
     */
    boolean isWithinTaskWindow(TaskFacts task, Instant now) {
        if (task.startDate() == null) {
            return false;
        }
        ZoneId zone = properties.resolveZone();
        int grace = properties.getTaskWindowGraceMinutes();

        LocalTime startTime = task.startTime() != null ? task.startTime() : LocalTime.MIDNIGHT;
        LocalDate endDate = task.endDate() != null ? task.endDate() : task.startDate();

        ZonedDateTime from = task.startDate().atTime(startTime).atZone(zone).minusMinutes(grace);
        ZonedDateTime to = endDate.plusDays(1).atStartOfDay(zone).plusMinutes(grace); // end of end_date

        return !now.isBefore(from.toInstant()) && now.isBefore(to.toInstant());
    }

    // ---------------------------------------------------------------- helpers

    private static byte[] usableReference(byte[] reference) {
        return reference != null && reference.length > 0 ? reference : null;
    }

    private static void requireImage(byte[] image, String name) {
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException(name + " must not be null or empty");
        }
    }
}
