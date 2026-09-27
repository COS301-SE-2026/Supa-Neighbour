package com.app.api.vision;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
 
import javax.imageio.ImageIO;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.util.retry.Retry;
import java.util.concurrent.TimeoutException;
 


@Component
@ConditionalOnProperty(prefix = "supaneighbour.vision", name = "mode", havingValue = "gemini")
public class GeminiVisionClient implements  VisionClient {

    private static final Logger LOG = LoggerFactory.getLogger(GeminiVisionClient.class);

    private static final String API_BASE = "https://generativelanguage.googleapis.com";

    private static final String JPEG_MIME = "image/jpeg";
    private static final String SYSTEM_PROMPT = "You verify whether a community-help task was completed by comparing a before photo and "
            + "an after photo. Judge only whether the task described appears done; ignore unrelated "
            + "background differences such as lighting or camera angle. Reply with JSON only, no "
            + "other text.";

    private final VisionProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code GeminiVisionClient}, initializing the WebClient with the Gemini API
     * base URL and logging the configured model.
     *
     * @param properties   the Gemini vision configuration properties
     * @param objectMapper the Jackson mapper for JSON serialization/deserialization
     */
    public GeminiVisionClient(VisionProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().baseUrl(API_BASE).build();
        if (properties.getGemini().getApiKey() == null || properties.getGemini().getApiKey().isBlank()) {
            LOG.warn("GeminiVisionClient is active but GEMINI_API_KEY is blank; AI verification will be unavailable until a key is configured.");
        } else {
            LOG.info("GeminiVisionClient active (model: {})", properties.getGemini().getModel());
        }
    }

    /**
     * Validates and normalizes the Gemini API key.
     *
     * @param apiKey the API key to validate; may be null or blank
     * @return the trimmed API key
     * @throws IllegalStateException if {@code apiKey} is null or blank
     */
    public static String requireApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is missing or empty. Set it before starting the backend so AI verification can call Gemini.");
        }
        return apiKey.trim();
    }

    /**
     * Describe the resident's reference ("before") photo: labels, a one-sentence insight, and how
     * confident the model is in that description. taskLooksComplete is not used.
     */
    @Override 
    public VisionResult analyzeBaseline(TaskContext task, byte[] referenceImage){
        requireImage(referenceImage, "referenceImage");
        String prompt = promptFor(task) + " This is the reference (\"before\") photo for the task. "
                + "Describe what it shows. Return JSON: {\"labels\": [string], "
                + "\"confidence\": number 0-1 = how sure you are of your description, "
                + "\"insight\": string (one sentence)}.";
        return call(prompt, List.of(referenceImage), false);
    }

    private VisionResult call(String prompt, List<byte[]> images, boolean expectVerdict){
        try {
            String apiKey = requireApiKey(properties.getGemini().getApiKey());
            properties.getGemini().setApiKey(apiKey);
        } catch (IllegalStateException e) {
            LOG.warn(e.getMessage());
            return VisionResult.unavailable(VisionResult.Outcome.ERROR, e.getMessage());
        }

        List<byte[]> resized;
        try{
            resized = images.stream().map(this::downscale).toList();
        }catch(IllegalArgumentException e){
            throw e;
        }

        Map<String, Object> body = requestBody(prompt, resized);
        String model = properties.getGemini().getModel();
        Duration timeout = Duration.ofSeconds(properties.getGemini().getTimeoutSeconds());

        String responseBody;
        try{
            responseBody = webClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .header("x-goog-api-key", properties.getGemini().getApiKey())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2))
                        .filter(t -> t instanceof WebClientResponseException w
                                && (w.getStatusCode().value() == 503
                                    || w.getStatusCode().value() == 429))
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .timeout(timeout)
                .onErrorMap(TimeoutException.class,
                        e -> new GeminiCallException(VisionResult.Outcome.ERROR, "timeout after " + timeout))
                .block();
        }catch(WebClientResponseException e){
            return VisionResult.unavailable(outcomeFor(e), "HTTP " + e.getStatusCode().value() + " " + safeBody(e));
        }catch(GeminiCallException e){
            return VisionResult.unavailable(e.outcome, e.detail);
        }catch(Exception e){
            return VisionResult.unavailable(VisionResult.Outcome.ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        return parse(responseBody, expectVerdict);
    }

    private Map<String, Object> requestBody(String prompt, List<byte[]> images) {
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", SYSTEM_PROMPT + "\n\n" + prompt));
        for (byte[] image : images) {
            parts.add(Map.of("inlineData", Map.of(
                    "mimeType", JPEG_MIME,
                    "data", Base64.getEncoder().encodeToString(image))));
        }

        return Map.of(
                "contents", List.of(Map.of("role", "user", "parts", parts)),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0));
    }

    /**
     * Maps a {@link WebClientResponseException} to a {@link VisionResult.Outcome}.
     * Returns {@code RATE_LIMITED} for HTTP 429, {@code CONTENT_FILTERED} for HTTP 400
     * responses whose body mentions "SAFETY", and {@code ERROR} otherwise.
     *
     * @param e the HTTP error response exception
     * @return the corresponding outcome
     */
    public static VisionResult.Outcome outcomeFor(WebClientResponseException e){
         int status = e.getStatusCode().value();
        if (status == 429) {
            return VisionResult.Outcome.RATE_LIMITED;
        }
        String bodyText = safeBody(e);
        if (status == 400 && bodyText != null && bodyText.toUpperCase(java.util.Locale.ROOT).contains("SAFETY")) {
            return VisionResult.Outcome.CONTENT_FILTERED;
        }
        return VisionResult.Outcome.ERROR;
    }

    private static String safeBody(WebClientResponseException e){
        try{
            String text = e.getResponseBodyAsString();
            return text == null ? null : text.substring(0, Math.min(500, text.length()));
        }catch(Exception ignored){
            return null;
        }
    }


    /**
     * Parses the Gemini API response body into a {@link VisionResult}, handling
     * content-filter blocks, missing text, and invalid JSON at both the envelope
     * and model-output levels.
     *
     * @param responseBody  the raw JSON response from the Gemini API
     * @param expectVerdict whether the response must include a {@code taskLooksComplete} verdict
     * @return the parsed result, or an unavailable result describing the failure
     */
    public VisionResult parse(String responseBody, boolean expectVerdict) {
        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (IOException e) {
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE, "response was not JSON");
        }
 
        JsonNode blockReason = root.path("promptFeedback").path("blockReason");
        if (!blockReason.isMissingNode() && !blockReason.isNull()) {
            return VisionResult.unavailable(VisionResult.Outcome.CONTENT_FILTERED, "blocked: " + blockReason.asText());
        }
 
        JsonNode candidate = root.path("candidates").path(0);
        String finishReason = candidate.path("finishReason").asText("");
        if ("SAFETY".equals(finishReason) || "PROHIBITED_CONTENT".equals(finishReason)) {
            return VisionResult.unavailable(VisionResult.Outcome.CONTENT_FILTERED, "finishReason: " + finishReason);
        }
 
        String text = candidate.path("content").path("parts").path(0).path("text").asText(null);
        if (text == null || text.isBlank()) {
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE,
                    "no text in response (finishReason: " + finishReason + ")");
        }
 
        JsonNode answer;
        try {
            answer = objectMapper.readTree(text);
        } catch (IOException e) {
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE, "model output was not valid JSON");
        }
 
        List<String> labels = new ArrayList<>();
        answer.path("labels").forEach(n -> labels.add(n.asText()));
        double confidence = answer.path("confidence").asDouble(0.0);
        String insight = answer.path("insight").asText(null);
        boolean taskLooksComplete = expectVerdict && answer.path("taskLooksComplete").asBoolean(false);
 
        if (expectVerdict && !answer.has("taskLooksComplete")) {
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE, "response missing taskLooksComplete");
        }
 
        return VisionResult.ok(labels, confidence, insight, taskLooksComplete);
    }

    /**
     * Downscales the given image so its longest edge does not exceed
     * {@code properties.getGemini().getMaxImageEdgePx()}, and re-encodes it as JPEG.
     * Returns the original bytes' re-encoded form unchanged in size if already within bounds.
     *
     * @param original the original image bytes
     * @return the downscaled (or unchanged) image encoded as JPEG
     * @throws IllegalArgumentException if the bytes cannot be decoded or are unsupported/corrupt
     * @throws IllegalStateException    if the image cannot be re-encoded as JPEG
     */
    public byte[] downscale(byte[] original){
        BufferedImage image;

        try{
            image = ImageIO.read(new ByteArrayInputStream(original));
        }catch(IOException e){
            throw new IllegalArgumentException("Could not decode image", e);
        }

        if(image == null){
            throw new IllegalArgumentException("Unsupported or corrupt image data");
        }

        int maxEdge = properties.getGemini().getMaxImageEdgePx();
        int width = image.getWidth();
        int height = image.getHeight();
        double scale = Math.min(1.0, maxEdge / (double) Math.max(width, height));

        BufferedImage toEncode = image;

        if(scale < 1.0){
            int newWidth = Math.max(1, (int) Math.round(width * scale));
            int newHeight = Math.max(1, (int) Math.round(height * scale));
            BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();
            toEncode = scaled;
        }

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(toEncode, "jpg", out);
            return out.toByteArray();
        }catch(IOException e){
            throw new IllegalStateException("Could not re-encode image", e);
        }
    }

    private static String promptFor(TaskContext task){
        StringBuilder sb = new StringBuilder("Task: ").append(orBlank(task.title()));

        if(task.instructions() != null && !task.instructions().isBlank()){
            sb.append(". Instructions: ").append(task.instructions());
        }
        if (task.taskType() != null && !task.taskType().isBlank()) {
            sb.append(". Type: ").append(task.taskType());
        }
        return sb.append(".").toString();
    }

    private static String orBlank(String s){
        return s == null ? "" : s;
    }

    private static void requireImage(byte[] image, String name){
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException(name + " must not be null or empty");
        }
    }
    /**
     * Compare the reference ("before") photo with the helper's completion ("after") photo and
     * judge whether the task looks done.
     */
    @Override
    public VisionResult compare(TaskContext task, byte[] referenceImage, byte[] completionImage) {
        requireImage(referenceImage, "referenceImage");
        requireImage(completionImage, "completionImage");
        String prompt = promptFor(task) + " Image 1 is the reference (\"before\") photo. Image 2 is "
                + "the completion (\"after\") photo. Return JSON: {\"labels\": [string], "
                + "\"confidence\": number 0-1 = how sure you are of your taskLooksComplete verdict "
                + "(not the probability that the task is done), \"insight\": string (one sentence), "
                + "\"taskLooksComplete\": boolean}.";
        return call(prompt, List.of(referenceImage, completionImage), true);
    }

    private static final class GeminiCallException extends RuntimeException {
        final VisionResult.Outcome outcome;
        final String detail;
 
        GeminiCallException(VisionResult.Outcome outcome, String detail) {
            this.outcome = outcome;
            this.detail = detail;
        }
    }
}
