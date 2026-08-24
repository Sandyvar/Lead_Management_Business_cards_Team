package com.project.leadcrm.controller;

import com.project.leadcrm.dto.LeadRequest;
import com.project.leadcrm.dto.LeadResponse;
import com.project.leadcrm.model.LeadStatus;
import com.project.leadcrm.service.LeadService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    Page<LeadResponse> findAll(@RequestParam(required = false) LeadStatus status, Pageable pageable) {
        return leadService.findAll(status, pageable);
    }

    @GetMapping("/{id}")
    LeadResponse findById(@PathVariable Long id) {
        return leadService.findById(id);
    }

    @PostMapping
    ResponseEntity<LeadResponse> create(@Valid @RequestBody LeadRequest request) {
        LeadResponse response = leadService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/leads/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    LeadResponse update(@PathVariable Long id, @Valid @RequestBody LeadRequest request) {
        return leadService.update(id, request);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        leadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
