package com.project.leadcrm.controller;

import com.project.leadcrm.dto.ApiResponse;
import com.project.leadcrm.dto.CaptureLeadRequestDto;
import com.project.leadcrm.dto.LeadDto;
import com.project.leadcrm.service.LeadCaptureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads/capture")
@RequiredArgsConstructor
@Tag(name = "Lead Capture & Scoring", description = "Automated Inbound Lead Capture from QR Codes, Digital Visiting Cards, and Web Forms")
public class LeadCaptureController {

    private final LeadCaptureService leadCaptureService;

    @PostMapping
    @Operation(summary = "Capture lead from QR scan, digital visiting card, or website form (with auto-scoring & alert)")
    public ResponseEntity<ApiResponse<LeadDto>> captureLead(@Valid @RequestBody CaptureLeadRequestDto requestDto) {
        LeadDto captured = leadCaptureService.captureLead(requestDto);
        return new ResponseEntity<>(ApiResponse.success("Lead captured and scored successfully", captured), HttpStatus.CREATED);
    }
}
