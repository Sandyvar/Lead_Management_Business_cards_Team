package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.ScheduleFollowupDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Followup;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.ActivityType;
import com.project.leadcrm.model.enums.FollowupStatus;
import com.project.leadcrm.model.enums.NotificationType;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.FollowupRepository;
import com.project.leadcrm.repository.LeadRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import com.project.leadcrm.service.FollowupService;
import com.project.leadcrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowupServiceImpl implements FollowupService {

    private final FollowupRepository followupRepository;
    private final LeadRepository leadRepository;
    private final EmployeeRepository employeeRepository;
    private final ActivityTimelineService timelineService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public FollowupResponseDto scheduleFollowup(ScheduleFollowupDto dto) {
        Lead lead = leadRepository.findById(dto.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + dto.getLeadId()));

        Employee employee = null;
        if (dto.getEmployeeId() != null) {
            employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + dto.getEmployeeId()));
        } else if (lead.getAssignedEmployee() != null) {
            employee = lead.getAssignedEmployee();
        }

        Followup followup = Followup.builder()
                .lead(lead)
                .assignedEmployee(employee)
                .followupDate(dto.getFollowupDate())
                .followupType(dto.getFollowupType())
                .status(FollowupStatus.PENDING)
                .notes(dto.getNotes())
                .reminderSent(false)
                .overdueAlertSent(false)
                .createdAt(LocalDateTime.now())
                .build();

        Followup saved = followupRepository.save(followup);

        // Update Lead's nextFollowup date
        lead.setNextFollowup(dto.getFollowupDate());
        leadRepository.save(lead);

        // Log in Activity Timeline
        timelineService.logActivity(
                lead.getId(),
                employee != null ? employee.getId() : null,
                ActivityType.FOLLOWUP_SCHEDULED,
                "Follow-up Scheduled: " + dto.getFollowupType(),
                "Follow-up scheduled for " + dto.getFollowupDate() + (dto.getNotes() != null ? ". Notes: " + dto.getNotes() : ""),
                null,
                dto.getFollowupDate().toString()
        );

        // Send Notification if employee assigned
        if (employee != null) {
            notificationService.createNotification(NotificationRequestDto.builder()
                    .employeeId(employee.getId())
                    .title("New Follow-up Scheduled")
                    .message("A " + dto.getFollowupType() + " follow-up with " + lead.getCustomerName() + 
                             (lead.getCompanyName() != null ? " (" + lead.getCompanyName() + ")" : "") + 
                             " has been scheduled for " + dto.getFollowupDate())
                    .type(NotificationType.IN_APP)
                    .referenceType("FOLLOWUP")
                    .referenceId(saved.getId())
                    .build());
        }

        log.info("Scheduled follow-up ID: {} for Lead ID: {} at {}", saved.getId(), lead.getId(), dto.getFollowupDate());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public FollowupResponseDto completeFollowup(Long followupId, String outcomeNotes) {
        Followup followup = followupRepository.findById(followupId)
                .orElseThrow(() -> new RuntimeException("Followup not found with ID: " + followupId));

        followup.setStatus(FollowupStatus.COMPLETED);
        followup.setCompletedAt(LocalDateTime.now());
        if (outcomeNotes != null && !outcomeNotes.trim().isEmpty()) {
            String updatedNotes = (followup.getNotes() != null ? followup.getNotes() + " | Outcome: " : "Outcome: ") + outcomeNotes;
            followup.setNotes(updatedNotes);
        }

        Followup saved = followupRepository.save(followup);

        // Update Lead last contact date
        Lead lead = followup.getLead();
        if (lead != null) {
            lead.setLastContactDate(LocalDateTime.now());
            leadRepository.save(lead);

            // Log completion in timeline
            timelineService.logActivity(
                    lead.getId(),
                    followup.getAssignedEmployee() != null ? followup.getAssignedEmployee().getId() : null,
                    ActivityType.FOLLOWUP_COMPLETED,
                    "Follow-up Completed: " + followup.getFollowupType(),
                    outcomeNotes != null ? outcomeNotes : "Follow-up marked as completed",
                    FollowupStatus.PENDING.name(),
                    FollowupStatus.COMPLETED.name()
            );
        }

        log.info("Completed follow-up ID: {}", followupId);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowupResponseDto getFollowupById(Long id) {
        Followup followup = followupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Followup not found with ID: " + id));
        return mapToDto(followup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowupResponseDto> getFollowupsForToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return followupRepository.findTodayFollowups(startOfDay, endOfDay)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowupResponseDto> getOverdueFollowups() {
        return followupRepository.findByStatusAndFollowupDateBefore(FollowupStatus.PENDING, LocalDateTime.now())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowupResponseDto> getFollowupsByLeadId(Long leadId) {
        return followupRepository.findByLeadIdOrderByFollowupDateDesc(leadId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowupResponseDto> getFollowupsByEmployeeId(Long employeeId) {
        return followupRepository.findByAssignedEmployeeId(employeeId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FollowupResponseDto mapToDto(Followup f) {
        boolean isOverdue = f.getStatus() == FollowupStatus.PENDING && f.getFollowupDate().isBefore(LocalDateTime.now());
        return FollowupResponseDto.builder()
                .id(f.getId())
                .leadId(f.getLead() != null ? f.getLead().getId() : null)
                .customerName(f.getLead() != null ? f.getLead().getCustomerName() : null)
                .companyName(f.getLead() != null ? f.getLead().getCompanyName() : null)
                .leadMobile(f.getLead() != null ? f.getLead().getMobile() : null)
                .assignedEmployeeId(f.getAssignedEmployee() != null ? f.getAssignedEmployee().getId() : null)
                .assignedEmployeeName(f.getAssignedEmployee() != null ? f.getAssignedEmployee().getName() : null)
                .followupDate(f.getFollowupDate())
                .followupType(f.getFollowupType())
                .status(f.getStatus())
                .notes(f.getNotes())
                .reminderSent(f.getReminderSent())
                .overdueAlertSent(f.getOverdueAlertSent())
                .completedAt(f.getCompletedAt())
                .createdAt(f.getCreatedAt())
                .isOverdue(isOverdue)
                .build();
    }
}
