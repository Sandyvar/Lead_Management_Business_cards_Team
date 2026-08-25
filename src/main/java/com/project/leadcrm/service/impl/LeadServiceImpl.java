package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.*;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.ActivityType;
import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.model.enums.LeadSource;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.model.enums.NotificationType;
import com.project.leadcrm.model.enums.Priority;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.LeadRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import com.project.leadcrm.service.LeadService;
import com.project.leadcrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final EmployeeRepository employeeRepository;
    private final ActivityTimelineService timelineService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public LeadDto createLead(CreateLeadDto dto) {
        log.info("Creating new lead for customer: {}", dto.getCustomerName());

        Employee assignedEmployee = null;
        if (dto.getAssignedEmployeeId() != null) {
            assignedEmployee = employeeRepository.findById(dto.getAssignedEmployeeId()).orElse(null);
        }

        Lead lead = Lead.builder()
                .customerName(dto.getCustomerName())
                .companyName(dto.getCompanyName())
                .mobile(dto.getMobile())
                .whatsapp(dto.getWhatsapp())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .requirement(dto.getRequirement())
                .leadSource(dto.getLeadSource() != null ? dto.getLeadSource() : LeadSource.MANUAL_ENTRY)
                .leadCategory(dto.getLeadCategory() != null ? dto.getLeadCategory() : LeadCategory.COLD)
                .leadStatus(dto.getLeadStatus() != null ? dto.getLeadStatus() : LeadStatus.NEW)
                .priority(dto.getPriority() != null ? dto.getPriority() : Priority.MEDIUM)
                .leadValue(dto.getLeadValue() != null ? dto.getLeadValue() : 0.0)
                .assignedEmployee(assignedEmployee)
                .createdDate(LocalDateTime.now())
                .nextFollowup(dto.getNextFollowup())
                .notes(dto.getNotes())
                .companyId(dto.getCompanyId())
                .build();

        Lead saved = leadRepository.save(lead);

        // 1. Log activity to timeline
        timelineService.logActivity(
                saved.getId(),
                assignedEmployee != null ? assignedEmployee.getId() : null,
                ActivityType.LEAD_CREATED,
                "Lead Created",
                "New lead profile created for " + saved.getCustomerName() + " via " + saved.getLeadSource(),
                null,
                saved.getLeadStatus().name()
        );

        // 2. If assigned to an employee upon creation, notify them
        if (assignedEmployee != null) {
            notificationService.sendNotification(NotificationRequestDto.builder()
                    .employeeId(assignedEmployee.getId())
                    .title("New Lead Assigned: " + saved.getCustomerName())
                    .message("You have been assigned a new lead from " + (saved.getCompanyName() != null ? saved.getCompanyName() : "individual"))
                    .type(NotificationType.IN_APP)
                    .referenceType("LEAD")
                    .referenceId(saved.getId())
                    .build());
        }

        return LeadDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadDto getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found with ID: " + id));
        return LeadDto.fromEntity(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(LeadDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsByStatus(LeadStatus status) {
        return leadRepository.findByLeadStatus(status).stream()
                .map(LeadDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsByEmployee(Long employeeId) {
        return leadRepository.findByAssignedEmployeeId(employeeId).stream()
                .map(LeadDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeadDto updateLeadStatus(Long id, UpdateLeadStatusDto updateDto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found with ID: " + id));

        LeadStatus oldStatus = lead.getLeadStatus();
        lead.setLeadStatus(updateDto.getStatus());
        lead.setLastContactDate(LocalDateTime.now());

        if (updateDto.getStatus() == LeadStatus.WON) {
            timelineService.logActivity(
                    lead.getId(),
                    updateDto.getEmployeeId(),
                    ActivityType.CONVERTED_TO_CUSTOMER,
                    "Lead Won & Converted",
                    "Lead successfully converted to Customer status.",
                    oldStatus.name(),
                    LeadStatus.WON.name()
            );
        } else {
            timelineService.logActivity(
                    lead.getId(),
                    updateDto.getEmployeeId(),
                    ActivityType.STATUS_CHANGED,
                    "Status Changed to " + updateDto.getStatus(),
                    updateDto.getNote() != null ? updateDto.getNote() : "Pipeline stage updated",
                    oldStatus.name(),
                    updateDto.getStatus().name()
            );
        }

        Lead saved = leadRepository.save(lead);
        return LeadDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public LeadDto assignLead(Long id, AssignLeadDto assignDto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found with ID: " + id));

        Employee employee = employeeRepository.findById(assignDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + assignDto.getEmployeeId()));

        String oldEmployeeName = lead.getAssignedEmployee() != null ? lead.getAssignedEmployee().getName() : "Unassigned";
        lead.setAssignedEmployee(employee);
        Lead saved = leadRepository.save(lead);

        // 1. Log to Timeline
        timelineService.logActivity(
                lead.getId(),
                assignDto.getAssignedByEmployeeId(),
                ActivityType.ASSIGNED_TO_EMPLOYEE,
                "Lead Reassigned",
                "Assigned to " + employee.getName() + (assignDto.getNote() != null ? ". Note: " + assignDto.getNote() : ""),
                oldEmployeeName,
                employee.getName()
        );

        // 2. Send Notification to Employee
        notificationService.sendNotification(NotificationRequestDto.builder()
                .employeeId(employee.getId())
                .title("Lead Assigned: " + lead.getCustomerName())
                .message("You have been assigned lead: " + lead.getCustomerName() + " (" + (lead.getCompanyName() != null ? lead.getCompanyName() : "No company") + ")")
                .type(NotificationType.IN_APP)
                .referenceType("LEAD")
                .referenceId(lead.getId())
                .build());

        return LeadDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new IllegalArgumentException("Lead not found with ID: " + id);
        }
        leadRepository.deleteById(id);
    }
}
