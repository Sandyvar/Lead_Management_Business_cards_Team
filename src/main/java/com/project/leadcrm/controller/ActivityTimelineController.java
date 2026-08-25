package com.project.leadcrm.controller;

import com.project.leadcrm.dto.ActivityTimelineDto;
import com.project.leadcrm.dto.ApiResponse;
import com.project.leadcrm.dto.CreateNoteRequestDto;
import com.project.leadcrm.service.ActivityTimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Activity Timeline API", description = "Endpoints for Lead chronological history, audit logging, and internal notes (Day 2-3 Task)")
public class ActivityTimelineController {

    private final ActivityTimelineService timelineService;

    @GetMapping("/leads/{leadId}/timeline")
    @Operation(summary = "Get complete chronological activity history for a lead")
    public ResponseEntity<ApiResponse<List<ActivityTimelineDto>>> getLeadTimeline(@PathVariable Long leadId) {
        List<ActivityTimelineDto> timeline = timelineService.getTimelineForLead(leadId);
        return ResponseEntity.ok(ApiResponse.success("Lead activity timeline retrieved successfully", timeline));
    }

    @PostMapping("/leads/{leadId}/timeline/note")
    @Operation(summary = "Add an internal operational note to a lead's timeline")
    public ResponseEntity<ApiResponse<ActivityTimelineDto>> addLeadNote(
            @PathVariable Long leadId,
            @Valid @RequestBody CreateNoteRequestDto requestDto) {
        ActivityTimelineDto result = timelineService.addNote(leadId, requestDto.getEmployeeId(), requestDto.getNote());
        return new ResponseEntity<>(ApiResponse.success("Note added to timeline successfully", result), HttpStatus.CREATED);
    }

    @GetMapping("/timeline/recent")
    @Operation(summary = "Get latest activity stream across all CRM leads (for manager/employee dashboards)")
    public ResponseEntity<ApiResponse<List<ActivityTimelineDto>>> getRecentActivities(
            @RequestParam(defaultValue = "20") int limit) {
        List<ActivityTimelineDto> recent = timelineService.getRecentActivities(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent activities retrieved successfully", recent));
    }
}
