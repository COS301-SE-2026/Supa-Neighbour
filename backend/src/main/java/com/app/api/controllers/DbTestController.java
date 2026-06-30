package com.app.api.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used for testing database connectivity.
 */

@RestController
public class DbTestController {
    
    private final JdbcTemplate jdbcTemplate;
    /**
     * Constructs the database test controller.
     *
     * @param jdbcTemplate template used to execute SQL queries
     */

    public DbTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Database connection test endpoint.
     *
     * @return database status message
     */
    @GetMapping("/db-test")
    public String testDb() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return "DB OK: " + result;
    }
}
