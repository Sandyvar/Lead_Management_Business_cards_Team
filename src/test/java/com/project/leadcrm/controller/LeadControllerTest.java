package com.project.leadcrm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/leads/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Avery Stone")))
                .andExpect(jsonPath("$.email", is("avery@example.com")))
                .andExpect(jsonPath("$.status", is("NEW")));

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email", is("avery@example.com")));
    }

    @Test
    void updatesAndDeletesLead() throws Exception {
        String createPayload = """
                {
                  "name": "Blair Kim",
                  "email": "blair@example.com",
                  "phone": "+15555550124",
                  "company": "Northstar",
                  "status": "NEW",
                  "notes": "Requested a follow-up"
                }
                """;

        String location = mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String updatePayload = """
                {
                  "name": "Blair Kim",
                  "email": "blair@example.com",
                  "phone": "+15555550124",
                  "company": "Northstar",
                  "status": "QUALIFIED",
                  "notes": "Demo scheduled"
                }
                """;

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("QUALIFIED")))
                .andExpect(jsonPath("$.notes", is("Demo scheduled")));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company", is("Northstar")));

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidLead() throws Exception {
        String payload = """
                {
                  "name": "",
                  "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }
}
