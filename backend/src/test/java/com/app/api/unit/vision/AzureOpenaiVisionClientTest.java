package com.app.api.unit.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import com.app.api.vision.*;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Covers response parsing and image downscaling without a network call, same approach as
 * GeminiVisionClientTest. The HTTP call itself (endpoint, deployment path, api-key header) is not
 * exercised here - that needs a real deployment, and is the thing to check by hand once Gendac
 * confirms the resource/deployment to use (see the guide, §5.1/§7).
 */
class AzureOpenAiVisionClientTest {

    private final VisionProperties properties = new VisionProperties();
    private final AzureOpenAiVisionClient client;

    AzureOpenAiVisionClientTest() {
        properties.getAzureOpenai().setEndpoint("https://example.openai.azure.com");
        properties.getAzureOpenai().setDeployment("gpt-4o-mini");
        properties.getAzureOpenai().setKey("test-key");
        client = new AzureOpenAiVisionClient(properties, new ObjectMapper());
    }

    // ---------------------------------------------------------------- parse(): compare (expectVerdict = true)

    @Test
    void parsesACompleteVerdict() {
        String body = chatEnvelope("{\"labels\":[\"tap\",\"sink\"],\"confidence\":0.87,"
                + "\"insight\":\"The tap is no longer dripping.\",\"taskLooksComplete\":true}", "stop");

        VisionResult r = client.parse(body, true);

        assertTrue(r.available());
        assertEquals(0.87, r.confidence(), 1e-9);
        assertTrue(r.taskLooksComplete());
        assertEquals("The tap is no longer dripping.", r.insight());
        assertEquals(2, r.labels().size());
    }

    @Test
    void parsesAnIncompleteVerdict() {
        String body = chatEnvelope("{\"labels\":[],\"confidence\":0.6,\"insight\":\"Still dripping.\","
                + "\"taskLooksComplete\":false}", "stop");

        VisionResult r = client.parse(body, true);

        assertTrue(r.available());
        assertFalse(r.taskLooksComplete());
    }

    @Test
    void missingTaskLooksCompleteIsInvalidWhenAVerdictWasExpected() {
        String body = chatEnvelope("{\"labels\":[],\"confidence\":0.6,\"insight\":\"x\"}", "stop");

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    // ---------------------------------------------------------------- parse(): baseline (expectVerdict = false)

    @Test
    void baselineDoesNotRequireTaskLooksComplete() {
        String body = chatEnvelope("{\"labels\":[\"tap\"],\"confidence\":0.9,\"insight\":\"A dripping tap.\"}", "stop");

        VisionResult r = client.parse(body, false);

        assertTrue(r.available());
        assertFalse(r.taskLooksComplete());   // never claims completion
    }

    // ---------------------------------------------------------------- content filter

    @Test
    void finishReasonContentFilterIsContentFiltered() {
        String body = chatEnvelope("{}", "content_filter");

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.CONTENT_FILTERED, r.outcome());
    }

    // ---------------------------------------------------------------- malformed responses

    @Test
    void notJsonAtAllIsInvalidResponse() {
        VisionResult r = client.parse("<html>not json</html>", true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    @Test
    void noChoicesIsInvalidResponse() {
        VisionResult r = client.parse("{}", true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    @Test
    void modelContentThatIsNotJsonIsInvalidResponse() {
        String body = chatEnvelopeRaw("Sure, here is my answer: it looks done!", "stop");

        VisionResult r = client.parse(body, true);

        assertFalse(r.available());
        assertEquals(VisionResult.Outcome.INVALID_RESPONSE, r.outcome());
    }

    // ---------------------------------------------------------------- HTTP status mapping

    @Test
    void statusCodesMapToTheRightOutcome() {
        assertEquals(VisionResult.Outcome.RATE_LIMITED,
                AzureOpenAiVisionClient.outcomeFor(responseException(429, "rate limit exceeded")));
        assertEquals(VisionResult.Outcome.CONTENT_FILTERED,
                AzureOpenAiVisionClient.outcomeFor(responseException(400,
                        "{\"error\":{\"code\":\"content_filter\",\"message\":\"blocked\"}}")));
        assertEquals(VisionResult.Outcome.ERROR,
                AzureOpenAiVisionClient.outcomeFor(responseException(401, "Access denied")));
        assertEquals(VisionResult.Outcome.ERROR,
                AzureOpenAiVisionClient.outcomeFor(responseException(404, "DeploymentNotFound")));
        assertEquals(VisionResult.Outcome.ERROR,
                AzureOpenAiVisionClient.outcomeFor(responseException(500, "internal error")));
    }

    // ---------------------------------------------------------------- downscaling (shares logic shape with Gemini's)

    @Test
    void largeImageIsShrunkToTheConfiguredEdge() throws IOException {
        properties.getAzureOpenai().setMaxImageEdgePx(200);
        byte[] big = png(2000, 1000);

        byte[] small = client.downscale(big);

        BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(small));
        assertEquals(200, decoded.getWidth());
        assertEquals(100, decoded.getHeight());
    }

    @Test
    void smallImageIsNotUpscaled() throws IOException {
        properties.getAzureOpenai().setMaxImageEdgePx(1600);
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

    /** Wraps a JSON object as the model's answer, the way Azure nests it under choices[0].message.content. */
    private static String chatEnvelope(String answerJson, String finishReason) {
        return chatEnvelopeRaw(answerJson, finishReason);
    }

    private static String chatEnvelopeRaw(String modelText, String finishReason) {
        String escaped = modelText.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"choices\":[{\"finish_reason\":\"" + finishReason + "\","
                + "\"message\":{\"role\":\"assistant\",\"content\":\"" + escaped + "\"}}]}";
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