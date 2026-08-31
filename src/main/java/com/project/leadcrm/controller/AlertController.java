package com.project.leadcrm.controller;

import com.project.leadcrm.dto.ApiResponse;
import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.OverdueAlertReportDto;
import com.project.leadcrm.service.OverdueAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts & Automation", description = "Endpoints for overdue scan automation and alert management")
public class AlertController {

    private final OverdueAlertService overdueAlertService;

    @PostMapping("/overdue/scan")
    @Operation(summary = "Manually trigger the overdue follow-up scanner and dispatch alerts")
    public ResponseEntity<ApiResponse<OverdueAlertReportDto>> triggerOverdueScan() {
        OverdueAlertReportDto report = overdueAlertService.checkAndDispatchOverdueAlerts();
        return ResponseEntity.ok(ApiResponse.success("Overdue scan executed successfully", report));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all active overdue follow-ups")
    public ResponseEntity<ApiResponse<List<FollowupResponseDto>>> getActiveOverdues() {
        List<FollowupResponseDto> list = overdueAlertService.getActiveOverdueFollowups();
        return ResponseEntity.ok(ApiResponse.success("Retrieved active overdue follow-ups", list));
    }
}
