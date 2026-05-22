package com.app.api.unit.controllers;

import com.app.api.controllers.DbTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(DbTestController.class)
public class DbTestControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate;


    @Test
    @WithMockUser
    void testDb_success() throws Exception
    {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), any(Class.class))).thenReturn(1);

        mockMvc.perform(get("/db-test")).andExpect(status().isOk()).andExpect(content().string("DB OK: 1"));
    }
}
