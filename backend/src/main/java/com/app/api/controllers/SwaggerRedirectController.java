package com.app.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/swagger-ui")
public class SwaggerRedirectController {

    /**
     * Redirect plain /swagger-ui and /swagger-ui/ requests to the Swagger UI index.
     *
     * @return the redirect target for Swagger UI
     */
    @GetMapping(value = {"", "/"})
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
