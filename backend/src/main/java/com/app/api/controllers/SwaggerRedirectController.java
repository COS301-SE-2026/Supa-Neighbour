package com.app.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/swagger-ui")
@Tag(name = "Swagger UI", description = "Redirect to Swagger UI documentation")
public class SwaggerRedirectController {

    /**
     * Redirect plain /swagger-ui and /swagger-ui/ requests to the Swagger UI index.
     *
     * @return the redirect target for Swagger UI
     */
    @GetMapping(value = {"", "/"})
    @Operation(
        summary = "Redirect to Swagger UI",
        description = "Redirects requests to /swagger-ui or /swagger-ui/ to /swagger-ui/index.html"
    )
    @ApiResponse(responseCode = "302", description = "Redirect to Swagger UI index.html")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
