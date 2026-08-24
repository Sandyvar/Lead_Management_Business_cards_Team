package com.project.leadcrm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReturnsLead() throws Exception {
        String payload = """
                {
                  "name": "Avery Stone",
                  "email": "avery@example.com",
                  "phone": "+15555550123",
                  "company": "Acme",
                  "status": "NEW",
                  "notes": "Interested in enterprise plan"
                }
                """;

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/leads/1"))
                .andExpect(jsonPath("$.name", is("Avery Stone")))
                .andExpect(jsonPath("$.email", is("avery@example.com")))
                .andExpect(jsonPath("$.status", is("NEW")));

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email", is("avery@example.com")));
    }

    @Test
    void rejectsInvalidLead() throws Exception {
        String payload = """
                {
                  "name": "",
                  "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }
}
