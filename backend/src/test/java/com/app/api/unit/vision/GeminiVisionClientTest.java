package com.app.api.unit.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.app.api.vision.*;
import com.app.api.verification.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Covers response parsing and image downscaling without a network call. The HTTP call itself
 * (call/requestBody/auth header) is not exercised here - that needs a live key or a mock server,
 * and is the thing to check by hand against the real API first (see the guide, §5.1/§7).
 */
class GeminiVisionClientTest {

    private final VisionProperties properties = new VisionProperties();
    private final GeminiVisionClient client = new GeminiVisionClient(properties, new ObjectMapper());

    // ---------------------------------------------------------------- parse(): compare (expectVerdict = true)

    @Test
    void parsesACompleteVerdict() {
        String body = geminiEnvelope("{\"labels\":[\"tap\",\"sink\"],\"confidence\":0.87,"
                + "\"insight\":\"The tap is no longer dripping.\",\"taskLooksComplete\":true}");

        VisionResult r = client.parse(body, true);

        assertTrue(r.available());
        assertEquals(0.87, r.confidence(), 1e-9);
        assertTrue(r.taskLooksComplete());
        assertEquals("The tap is no longer dripping.", r.insight());
        assertEquals(2, r.labels().size());
    }

    @Test
    void parsesAnIncompleteVerdict() {
        String body = geminiEnvelope("{\"labels\":[],\"confidence\":0.6,\"insight\":\"Still dripping.\","
                + "\"taskLooksComplete\":false}");

        VisionResult r = client.parse(body, true);

        assertTrue(r.available());
        assertFalse(r.taskLooksComplete());
    }

    @Test
    void missingTaskLooksCompleteIsInvalidWhenAVerdictWasExpected() {
        String body = geminiEnvelope("{\"labels\":[],\"confidence\":0.6,\"insight\":\"x\"}");

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    // ---------------------------------------------------------------- parse(): baseline (expectVerdict = false)

    @Test
    void baselineDoesNotRequireTaskLooksComplete() {
        String body = geminiEnvelope("{\"labels\":[\"tap\"],\"confidence\":0.9,\"insight\":\"A dripping tap.\"}");

        VisionResult r = client.parse(body, false);

        assertTrue(r.available());
        assertFalse(r.taskLooksComplete());   // never claims completion
    }

    // ---------------------------------------------------------------- safety / blocking

    @Test
    void promptLevelBlockIsContentFiltered() {
        String body = "{\"promptFeedback\":{\"blockReason\":\"SAFETY\"}}";

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.CONTENT_FILTERED, r.outcome());
    }

    @Test
    void candidateFinishReasonSafetyIsContentFiltered() {
        String body = "{\"candidates\":[{\"finishReason\":\"SAFETY\",\"content\":{\"parts\":[]}}]}";

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.CONTENT_FILTERED, r.outcome());
    }

    @Test
    void prohibitedContentIsContentFiltered() {
        String body = "{\"candidates\":[{\"finishReason\":\"PROHIBITED_CONTENT\",\"content\":{\"parts\":[]}}]}";

        assertEquals(VisionResult.Outcome.CONTENT_FILTERED, client.parse(body, true).outcome());
    }

    // ---------------------------------------------------------------- malformed responses

    @Test
    void notJsonAtAllIsInvalidResponse() {
        VisionResult r = client.parse("<html>not json</html>", true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    @Test
    void noCandidatesIsInvalidResponse() {
        VisionResult r = client.parse("{}", true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    @Test
    void modelTextThatIsNotJsonIsInvalidResponse() {
        String body = geminiEnvelopeRaw("Sure, here is my answer: it looks done!");

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    // ---------------------------------------------------------------- HTTP status mapping

    @Test
    void statusCodesMapToTheRightOutcome() {
        assertEquals(VisionResult.Outcome.RATE_LIMITED,
                GeminiVisionClient.outcomeFor(responseException(429, "quota exceeded")));
        assertEquals(VisionResult.Outcome.CONTENT_FILTERED,
                GeminiVisionClient.outcomeFor(responseException(400, "Reason: SAFETY blocked")));
        assertEquals(VisionResult.Outcome.ERROR,
                GeminiVisionClient.outcomeFor(responseException(401, "API key not valid")));
        assertEquals(VisionResult.Outcome.ERROR,
                GeminiVisionClient.outcomeFor(responseException(500, "internal error")));
    }

    @Test
    void blankApiKeyFailsFast() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> GeminiVisionClient.requireApiKey("   "));

        assertTrue(ex.getMessage().contains("GEMINI_API_KEY"));
    }

    // ---------------------------------------------------------------- downscaling

    @Test
    void largeImageIsShrunkToTheConfiguredEdge() throws IOException {
        properties.getGemini().setMaxImageEdgePx(200);
        byte[] big = png(2000, 1000);

        byte[] small = client.downscale(big);

        BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(small));
        assertEquals(200, decoded.getWidth());
        assertEquals(100, decoded.getHeight());
    }

    @Test
    void smallImageIsNotUpscaled() throws IOException {
        properties.getGemini().setMaxImageEdgePx(1600);
        byte[] tiny = png(50, 40);

        byte[] result = client.downscale(tiny);

        BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(result));
        assertEquals(50, decoded.getWidth());
        assertEquals(40, decoded.getHeight());
    }

    @Test
    void undecodableBytesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> client.downscale("not an image".getBytes()));
    }

    // ---------------------------------------------------------------- helpers

    /** Wraps a JSON object as the model's answer text, the way Gemini nests it under candidates[0].content.parts[0].text. */
    private static String geminiEnvelope(String answerJson) {
        return geminiEnvelopeRaw(answerJson);
    }

    private static String geminiEnvelopeRaw(String modelText) {
        String escaped = modelText.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"" + escaped + "\"}]},\"finishReason\":\"STOP\"}]}";
    }

    private static org.springframework.web.reactive.function.client.WebClientResponseException responseException(
            int status, String body) {
        return org.springframework.web.reactive.function.client.WebClientResponseException.create(
                status, "status " + status, null, body.getBytes(), null);
    }

    private static byte[] png(int w, int h) throws IOException {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }
}