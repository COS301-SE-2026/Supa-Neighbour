package com.app.api.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that provides a simple health check endpoint.
 * <p>
 * This controller is used to verify that the application is running and responsive.
 * It can be utilized by monitoring tools, load balancers, or CI/CD pipelines
 * to confirm the service availability.
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2026-08-18
 */
@RestController
public class PingController {

    /**
     * Handles GET requests to the health check endpoint.
     * <p>
     * This method responds with a simple "pong" message to confirm that the
     * application is operational and the REST API is accessible.
     * </p>
     * 
     * @return a {@link String} containing "pong" to indicate the service is alive
     * 
     * @example
     * <pre>
     * GET /api/ping
     * Response: "pong"
     * </pre>
     */
    @GetMapping("/api/ping")
    public String ping() {
        return "pong";
    }
}
