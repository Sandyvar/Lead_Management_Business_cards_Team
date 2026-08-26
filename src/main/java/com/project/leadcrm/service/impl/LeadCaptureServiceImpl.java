package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.CaptureLeadRequestDto;
import com.project.leadcrm.dto.LeadDto;
import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.*;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.LeadRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import com.project.leadcrm.service.LeadCaptureService;
import com.project.leadcrm.service.LeadScoringService;
import com.project.leadcrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadCaptureServiceImpl implements LeadCaptureService {

    private final LeadRepository leadRepository;
    private final EmployeeRepository employeeRepository;
    private final ActivityTimelineService timelineService;
    private final NotificationService notificationService;
    private final LeadScoringService scoringService;

    @Override
    @Transactional
    public LeadDto captureLead(CaptureLeadRequestDto request) {
        log.info("Processing inbound lead capture for mobile: {}, name: {}", request.getMobile(), request.getCustomerName());

        int incomingScore = scoringService.calculateScore(request);
        LeadSource source = request.getSource() != null ? request.getSource() : LeadSource.QR_CODE;

        // 1. Check if lead already exists by mobile or email
        Optional<Lead> existingOpt = leadRepository.findFirstByMobileOrEmail(request.getMobile(), request.getEmail());

        if (existingOpt.isPresent()) {
            Lead existing = existingOpt.get();
            log.info("Found existing lead ID: {}. Updating touchpoints and score.", existing.getId());

            int updatedScore = (existing.getLeadScore() != null ? existing.getLeadScore() : 0) + incomingScore;
            LeadCategory oldCategory = existing.getLeadCategory();
            LeadCategory newCategory = scoringService.evaluateCategory(updatedScore);

            existing.setLeadScore(updatedScore);
            existing.setLeadCategory(newCategory);
            existing.setLastContactDate(LocalDateTime.now());

            if (request.getRequirement() != null && !request.getRequirement().isBlank()) {
                String currentReq = existing.getRequirement() != null ? existing.getRequirement() + " | " : "";
                existing.setRequirement(currentReq + "Update: " + request.getRequirement());
            }

            Lead saved = leadRepository.save(existing);

            // Log activity
            timelineService.logActivity(
                    saved.getId(),
                    saved.getAssignedEmployee() != null ? saved.getAssignedEmployee().getId() : null,
                    ActivityType.NOTE_ADDED,
                    "Re-Engagement Touchpoint (+ " + incomingScore + " pts)",
                    "Lead re-engaged via " + source + ". New Score: " + updatedScore + " (" + newCategory + ")",
                    oldCategory != null ? oldCategory.name() : null,
                    newCategory.name()
            );

            // If escalated to HOT or WARM, notify assignee
            if (saved.getAssignedEmployee() != null && (newCategory == LeadCategory.HOT || newCategory != oldCategory)) {
                notificationService.sendNotification(NotificationRequestDto.builder()
                        .employeeId(saved.getAssignedEmployee().getId())
                        .title("🔥 Lead Activity Escalation: " + saved.getCustomerName())
                        .message("Lead has reached " + newCategory + " category with " + updatedScore + " score points!")
                        .type(NotificationType.IN_APP)
                        .referenceType("LEAD")
                        .referenceId(saved.getId())
                        .build());
            }

            return LeadDto.fromEntity(saved);
        }

        // 2. Create brand new lead
        Employee assignedEmployee = null;
        if (request.getAssignedEmployeeId() != null) {
            assignedEmployee = employeeRepository.findById(request.getAssignedEmployeeId()).orElse(null);
        }

        LeadCategory initialCategory = scoringService.evaluateCategory(incomingScore);
        Priority priority = initialCategory == LeadCategory.HOT ? Priority.HIGH :
                (initialCategory == LeadCategory.WARM ? Priority.MEDIUM : Priority.LOW);

        Lead newLead = Lead.builder()
                .customerName(request.getCustomerName())
                .companyName(request.getCompanyName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .requirement(request.getRequirement())
                .leadSource(source)
                .leadCategory(initialCategory)
                .leadStatus(LeadStatus.NEW)
                .priority(priority)
                .leadScore(incomingScore)
                .leadValue(0.0)
                .assignedEmployee(assignedEmployee)
                .createdDate(LocalDateTime.now())
                .lastContactDate(LocalDateTime.now())
                .companyId(request.getCompanyId() != null ? request.getCompanyId() : 1L)
                .build();

        Lead saved = leadRepository.save(newLead);

        // Log creation activity
        timelineService.logActivity(
                saved.getId(),
                assignedEmployee != null ? assignedEmployee.getId() : null,
                ActivityType.LEAD_CREATED,
                "Inbound Lead Captured (" + initialCategory + ")",
                "Captured via " + source + " with initial score of " + incomingScore + " points.",
                null,
                saved.getLeadStatus().name()
        );

        // Alert assigned employee
        if (assignedEmployee != null) {
            notificationService.sendNotification(NotificationRequestDto.builder()
                    .employeeId(assignedEmployee.getId())
                    .title("⚡ New Inbound Lead Captured: " + saved.getCustomerName())
                    .message("New " + initialCategory + " lead (" + incomingScore + " pts) captured via " + source)
                    .type(NotificationType.IN_APP)
                    .referenceType("LEAD")
                    .referenceId(saved.getId())
                    .build());
        }

        return LeadDto.fromEntity(saved);
    }
}
