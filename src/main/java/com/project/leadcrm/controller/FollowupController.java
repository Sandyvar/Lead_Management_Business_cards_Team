package com.project.leadcrm.controller;

import com.project.leadcrm.dto.ApiResponse;
import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.ScheduleFollowupDto;
import com.project.leadcrm.service.FollowupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/followups")
@RequiredArgsConstructor
@Tag(name = "Follow-up Management", description = "Endpoints for scheduling, completing, and tracking sales follow-ups")
public class FollowupController {

    private final FollowupService followupService;

    @PostMapping
    @Operation(summary = "Schedule a new follow-up for a lead")
    public ResponseEntity<ApiResponse<FollowupResponseDto>> scheduleFollowup(
            @Valid @RequestBody ScheduleFollowupDto dto) {
        FollowupResponseDto created = followupService.scheduleFollowup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Follow-up scheduled successfully", created));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark a follow-up as completed with optional outcome notes")
    public ResponseEntity<ApiResponse<FollowupResponseDto>> completeFollowup(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String outcomeNotes = (body != null) ? body.get("outcomeNotes") : null;
        FollowupResponseDto updated = followupService.completeFollowup(id, outcomeNotes);
        return ResponseEntity.ok(ApiResponse.success("Follow-up marked as completed", updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get follow-up details by ID")
    public ResponseEntity<ApiResponse<FollowupResponseDto>> getFollowupById(@PathVariable Long id) {
        FollowupResponseDto dto = followupService.getFollowupById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/today")
    @Operation(summary = "Get all follow-ups scheduled for today")
    public ResponseEntity<ApiResponse<List<FollowupResponseDto>>> getTodayFollowups() {
        List<FollowupResponseDto> list = followupService.getFollowupsForToday();
        return ResponseEntity.ok(ApiResponse.success("Retrieved today's follow-ups", list));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all currently overdue follow-ups")
    public ResponseEntity<ApiResponse<List<FollowupResponseDto>>> getOverdueFollowups() {
        List<FollowupResponseDto> list = followupService.getOverdueFollowups();
        return ResponseEntity.ok(ApiResponse.success("Retrieved overdue follow-ups", list));
    }

    @GetMapping("/lead/{leadId}")
    @Operation(summary = "Get all follow-up history for a specific lead")
    public ResponseEntity<ApiResponse<List<FollowupResponseDto>>> getFollowupsByLead(@PathVariable Long leadId) {
        List<FollowupResponseDto> list = followupService.getFollowupsByLeadId(leadId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get all follow-ups assigned to a specific employee")
    public ResponseEntity<ApiResponse<List<FollowupResponseDto>>> getFollowupsByEmployee(@PathVariable Long employeeId) {
        List<FollowupResponseDto> list = followupService.getFollowupsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
