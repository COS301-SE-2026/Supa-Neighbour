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
import java.util.Locale;
import java.util.Map;
 
import javax.imageio.ImageIO;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.app.api.vision.VisionClient.TaskContext;
import com.app.api.vision.VisionResult.Outcome;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component 
@ConditionalOnProperty(prefix = "supaneighbour.vision", name = "mode", havingValue = "azure-openai")
public class AzureOpenAiVisionClient implements VisionClient{
    
    private static final Logger LOG = LoggerFactory.getLogger(AzureOpenAiVisionClient.class);

    private static  final String JPEG_MIME = "image/jpeg";
    private static final String SYSTEM_PROMPT = "You verify whether a community-help task was completed by comparing a before photo and "
            + "an after photo. Judge only whether the task described appears done; ignore unrelated "
            + "background differences such as lighting or camera angle. Reply with JSON only, no "
            + "other text.";

    private final VisionProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

     /**
     * Creates a new client and prepares the underlying {@link WebClient}.
     *
     * @param properties   the vision configuration; its Azure OpenAI sub-config
     *                     must be populated (endpoint, deployment, key, etc.)
     * @param objectMapper Jackson mapper used to build request payloads and to
     *                     parse the model's JSON response
     */
    public AzureOpenAiVisionClient(
        VisionProperties properties, 
        ObjectMapper objectMapper
    ){
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().baseUrl(properties.getAzureOpenai().getEndpoint()).build();
        LOG.info("AzureOpenAiVisionClient active (deployment: {})", properties.getAzureOpenai().getDeployment());
    }

    /**
     * Analyses a single "before" image for a task and returns a description.
     *
     * <p>Asks the model to describe what the reference photo shows and to
     * return a confidence score for that description along with a one-sentence
     * insight.</p>
     *
     * @param task           contextual information about the task (title,
     *                       instructions, type); must not be {@code null}
     * @param referenceImage raw bytes of the "before" photo; must not be
     *                       {@code null} or empty
     * @return a {@link VisionResult} with labels, confidence, and insight;
     *         {@code taskLooksComplete} is always {@code false} for baselines
     * @throws IllegalArgumentException if {@code referenceImage} is null or empty
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

    /**
     * Compares a "before" and an "after" photo and asks the model whether the
     * task appears to have been completed.
     *
     * <p>The "before" image is presented as image 1 and the "after" image as
     * image 2. The model is asked to return labels, a confidence score (for its
     * verdict), a one-sentence insight, and a boolean {@code taskLooksComplete}
     * field.</p>
     *
     * @param task            contextual information about the task; must not be
     *                        {@code null}
     * @param referenceImage  raw bytes of the "before" photo; must not be
     *                        {@code null} or empty
     * @param completionImage raw bytes of the "after" photo; must not be
     *                        {@code null} or empty
     * @return a {@link VisionResult} containing the verdict and supporting
     *         information; on failure, an "unavailable" result is returned
     * @throws IllegalArgumentException if either image is null or empty
     */
    @Override 
    public VisionResult compare(TaskContext task, byte[]  referenceImage, byte[] completionImage){
        requireImage(referenceImage, "referenceImage");
        requireImage(completionImage, "completionImage");
        String prompt = promptFor(task) + " Image 1 is the reference (\"before\") photo. Image 2 is "
                + "the completion (\"after\") photo. Return JSON: {\"labels\": [string], "
                + "\"confidence\": number 0-1 = how sure you are of your taskLooksComplete verdict "
                + "(not the probability that the task is done), \"insight\": string (one sentence), "
                + "\"taskLooksComplete\": boolean}.";
        return call(prompt, List.of(referenceImage, completionImage), true);
    }

    /**
     * Sends a chat-completions request to Azure OpenAI with the given prompt
     * and images.
     *
     * <p>Images are downscaled via {@link #downscale(byte[])} before being
     * embedded as base64 data URLs. HTTP error responses, timeouts, and other
     * exceptions are converted into "unavailable" results with an appropriate
     * {@link Outcome}. The returned body (when successful) is delegated to
     * {@link #parse(String, boolean)}.</p>
     *
     * @param prompt        the user prompt to send
     * @param images        the image bytes to include in the request
     * @param expectVerdict {@code true} if the caller expects a
     *                      {@code taskLooksComplete} field in the model output
     * @return a {@link VisionResult}, possibly an "unavailable" one on failure
     */
    private VisionResult call(String prompt, List<byte[]> images, boolean expectVerdict){
        List<byte[]> resized = images.stream().map(this::downscale).toList();

        Map<String, Object> body = requestBody(prompt, resized);
        VisionProperties.AzureOpenai config = properties.getAzureOpenai();
        Duration timeout = Duration.ofSeconds(config.getTimeoutSeconds());

        String responseBody;
        try{
            responseBody = webClient.post().uri(
                "/openai/deployments/{deployment}/chat/completions?api-version={v}",
                config.getDeployment(), config.getApiVersion()
            ).header("api-key", config.getKey())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(timeout)
            .onErrorMap(java.util.concurrent.TimeoutException.class,
                e -> new AzureCallException(VisionResult.Outcome.ERROR, "timeout after " + timeout))
            .block();
        }catch(WebClientResponseException e){
            return VisionResult.unavailable(outcomeFor(e), "HTTP " + e.getStatusCode().value() + " " + safeBody(e));
        }catch(AzureCallException e){
            return VisionResult.unavailable(e.outcome, e.detail);
        }catch(Exception e){
            return VisionResult.unavailable(VisionResult.Outcome.ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        return parse(responseBody, expectVerdict);
    }

    /**
     * Builds the JSON request body for the Azure OpenAI chat-completions API.
     *
     * <p>The body contains a system message with {@link #SYSTEM_PROMPT}, a user
     * message with the prompt text followed by each image encoded as a base64
     * data URL, a {@code json_object} response format, temperature {@code 0},
     * and a {@code max_tokens} cap of 400.</p>
     *
     * @param prompt the user prompt text
     * @param images the (already resized) image bytes to embed
     * @return a map suitable for serialization as the request body
     */
    private Map<String, Object> requestBody(String prompt, List<byte[]> images){
        List<Map<String, Object>> content = new ArrayList<>();

        content.add(Map.of("type", "text", "text", prompt));
        for(byte[] image: images){
            String dataUrl = "data:" + JPEG_MIME + ";base64," + Base64.getEncoder().encodeToString(image);
            content.add(Map.of("type", "image_url", "image_url", Map.of("url", dataUrl)));
        }

        List<Map<String, Object>> messages = List.of(
            Map.of("role", "system", "content", SYSTEM_PROMPT),
            Map.of("role", "user", "content", content)
        );

        return Map.of(
            "messages", messages,
            "response_format", Map.of("type", "json_object"),
            "temperature", 0,
            "max_tokens", 400
        );
    }

    /**
     * Maps an HTTP error from Azure OpenAI to a {@link VisionResult.Outcome}.
     *
     * <p>HTTP 429 maps to {@link Outcome#RATE_LIMITED}. HTTP 400 whose body
     * mentions {@code content_filter} maps to
     * {@link Outcome#CONTENT_FILTERED}. Everything else maps to
     * {@link Outcome#ERROR}.</p>
     *
     * @param e the response exception from the WebClient
     * @return the corresponding outcome
     */
    public static VisionResult.Outcome outcomeFor(WebClientResponseException e){
        int status = e.getStatusCode().value();
        if(status == 429){
            return VisionResult.Outcome.RATE_LIMITED;
        }

        String bodyText = safeBody(e);
        if(status == 400 && bodyText!= null && bodyText.toLowerCase(Locale.ROOT).contains("content_filter")){
            return VisionResult.Outcome.CONTENT_FILTERED;
        }

        return VisionResult.Outcome.ERROR;
    }

    /**
     * Extracts at most the first 500 characters of an error response body,
     * returning {@code null} if the body cannot be read.
     *
     * @param e the response exception
     * @return a truncated body string, or {@code null} if unavailable
     */
    private static String safeBody(WebClientResponseException e) {
        try {
            String text = e.getResponseBodyAsString();
            return text == null ? null : text.substring(0, Math.min(500, text.length()));
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Builds the task-specific portion of the prompt from a {@link TaskContext}.
     *
     * <p>Always includes the title; appends instructions and task type when
     * present. The result ends with a period.</p>
     *
     * @param task the task context
     * @return the prompt prefix describing the task
     */
    private static String promptFor(TaskContext task) {
        StringBuilder sb = new StringBuilder("Task: ").append(orBlank(task.title()));
        if (task.instructions() != null && !task.instructions().isBlank()) {
            sb.append(". Instructions: ").append(task.instructions());
        }
        if (task.taskType() != null && !task.taskType().isBlank()) {
            sb.append(". Type: ").append(task.taskType());
        }
        return sb.append(".").toString();
    }
 
     /**
     * Returns the given string, or an empty string when it is {@code null}.
     *
     * @param s the string to check
     * @return {@code s} if non-null, otherwise {@code ""}
     */
    private static String orBlank(String s) {
        return s == null ? "" : s;
    }
 
    /**
     * Validates that an image byte array is non-null and non-empty.
     *
     * @param image the image bytes
     * @param name  the parameter name used in the exception message
     * @throws IllegalArgumentException if {@code image} is null or empty
     */
    private static void requireImage(byte[] image, String name) {
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException(name + " must not be null or empty");
        }
    }

    /**
     * Parses the Azure OpenAI chat-completions response into a
     * {@link VisionResult}.
     *
     * <p>Handles the following failure modes by returning an "unavailable"
     * result: malformed outer JSON, a {@code content_filter} finish reason, an
     * empty message, non-JSON message content, and (when a verdict is
     * expected) a missing {@code taskLooksComplete} field. Note that the
     * {@code taskLooksComplete} value is read from a JSON field named
     * {@code "task"}.</p>
     *
     * @param responseBody  the raw response body from Azure OpenAI
     * @param expectVerdict whether a {@code taskLooksComplete} verdict is
     *                      required
     * @return a parsed {@link VisionResult}, or an "unavailable" one on failure
     */
    public VisionResult parse(String responseBody, boolean expectVerdict){
        JsonNode root;
        try{
            root = objectMapper.readTree(responseBody);
        }catch(IOException e){
            return VisionResult.unavailable(
                VisionResult.Outcome.INVALID_RESPONSE, 
                "response was not JSON"
            );
        }

        JsonNode choice = root.path("choices").path(0);
        String finishReason = choice.path("finish_reason").asText("");
        if("content_filter".equals(finishReason)){
            return VisionResult.unavailable(VisionResult.Outcome.CONTENT_FILTERED, "finishReason: content_filter");
        }

        String text = choice.path("message").path("content").asText(null);
        if(text == null || text.isBlank()){
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE, 
                "no content in response (finish_reason: " + finishReason + ")"
            );
        }

        JsonNode answer;
        try{
            answer = objectMapper.readTree(text);
        } catch(IOException e){
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE, "model output was not valid JSON");
        }

        List<String> labels = new ArrayList<>();
        answer.path("labels").forEach( n -> labels.add(n.asText()));
        double confidence = answer.path("confidence").asDouble(0.0);
        String insight = answer.path("insight").asText(null);
        boolean taskLooksComplete = expectVerdict && answer.path("taskLooksComplete").asBoolean(false);

        if (expectVerdict && !answer.has("taskLooksComplete")) {
            return VisionResult.unavailable(VisionResult.Outcome.INVALID_RESPONSE, "response missing taskLooksComplete");
        }
 
        return VisionResult.ok(labels, confidence, insight, taskLooksComplete);
    }

    /**
     * Downscales an image so that its longest edge does not exceed
     * {@link VisionProperties.AzureOpenai#getMaxImageEdgePx()}, then re-encodes
     * it as JPEG.
     *
     * <p>If the image already fits within the configured limit it is still
     * re-encoded as JPEG. Bilinear interpolation is used when scaling.</p>
     *
     * @param original the original image bytes
     * @return the (possibly downscaled) JPEG bytes
     * @throws IllegalArgumentException if the bytes cannot be decoded or
     *                                  represent an unsupported/corrupt image
     * @throws IllegalStateException    if the image cannot be re-encoded
     */
    public byte[] downscale(byte[] original){
        BufferedImage image;
        try{
            image = ImageIO.read(new ByteArrayInputStream(original));
        } catch(IOException e){
            throw new IllegalArgumentException("Could not decode image", e);
        }

        if(image == null){
            throw new IllegalArgumentException("Unsupported or corrupt image data");
        }

        int maxEdge = properties.getAzureOpenai().getMaxImageEdgePx();
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
            return  out.toByteArray();
        }catch(IOException e){
            throw new IllegalStateException("Could not re-encode image", e);
        }
    }

    
    /**
     * Internal exception used to carry a {@link VisionResult.Outcome} and a
     * human-readable detail message out of the reactive pipeline so that it can
     * be converted into an "unavailable" {@link VisionResult}.
     */
    private static final class AzureCallException extends RuntimeException {
        final VisionResult.Outcome outcome;
        final String detail;
 
        AzureCallException(VisionResult.Outcome outcome, String detail) {
            this.outcome = outcome;
            this.detail = detail;
        }
    }
}
