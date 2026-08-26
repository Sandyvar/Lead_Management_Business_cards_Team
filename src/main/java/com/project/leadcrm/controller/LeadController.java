package com.project.leadcrm.controller;

import com.project.leadcrm.dto.*;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Tag(name = "Lead Management", description = "Core Lead CRUD, Assignment, and Pipeline Stage Transitions")
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @Operation(summary = "Create a new lead (automatically logs timeline event and alerts assignee)")
    public ResponseEntity<ApiResponse<LeadDto>> createLead(@Valid @RequestBody CreateLeadDto createLeadDto) {
        LeadDto created = leadService.createLead(createLeadDto);
        return new ResponseEntity<>(ApiResponse.success("Lead created successfully", created), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all leads or filter by status")
    public ResponseEntity<ApiResponse<List<LeadDto>>> getLeads(@RequestParam(required = false) LeadStatus status) {
        List<LeadDto> leads = (status != null) ? leadService.getLeadsByStatus(status) : leadService.getAllLeads();
        return ResponseEntity.ok(ApiResponse.success("Leads retrieved successfully", leads));
    }

    @GetMapping("/pipeline")
    @Operation(summary = "Get Kanban sales pipeline board (all stages grouped with counts, totals, and leads)")
    public ResponseEntity<ApiResponse<PipelineBoardDto>> getPipelineBoard() {
        PipelineBoardDto board = leadService.getPipelineBoard();
        return ResponseEntity.ok(ApiResponse.success("Pipeline Kanban board retrieved successfully", board));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lead details by ID")
    public ResponseEntity<ApiResponse<LeadDto>> getLeadById(@PathVariable Long id) {
        LeadDto lead = leadService.getLeadById(id);
        return ResponseEntity.ok(ApiResponse.success("Lead retrieved successfully", lead));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get leads assigned to a specific employee")
    public ResponseEntity<ApiResponse<List<LeadDto>>> getLeadsByEmployee(@PathVariable Long employeeId) {
        List<LeadDto> leads = leadService.getLeadsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Assigned leads retrieved successfully", leads));
    }

    @PutMapping("/{id}/stage")
    @Operation(summary = "Move lead to a new pipeline stage (automatically records stage transition in timeline)")
    public ResponseEntity<ApiResponse<LeadDto>> updateLeadStage(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadStatusDto updateDto) {
        LeadDto updated = leadService.updateLeadStatus(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("Lead stage updated successfully", updated));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign lead to an employee (triggers timeline update & notification alert)")
    public ResponseEntity<ApiResponse<LeadDto>> assignLead(
            @PathVariable Long id,
            @Valid @RequestBody AssignLeadDto assignDto) {
        LeadDto updated = leadService.assignLead(id, assignDto);
        return ResponseEntity.ok(ApiResponse.success("Lead assigned successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lead by ID")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(ApiResponse.success("Lead deleted successfully", null));
    }
}
