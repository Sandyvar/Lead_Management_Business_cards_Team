package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.OverdueAlertReportDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Followup;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.ActivityType;
import com.project.leadcrm.model.enums.FollowupStatus;
import com.project.leadcrm.model.enums.NotificationType;
import com.project.leadcrm.repository.FollowupRepository;
import com.project.leadcrm.repository.LeadRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import com.project.leadcrm.service.NotificationService;
import com.project.leadcrm.service.OverdueAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverdueAlertServiceImpl implements OverdueAlertService {

    private final FollowupRepository followupRepository;
    private final LeadRepository leadRepository;
    private final NotificationService notificationService;
    private final ActivityTimelineService timelineService;

    /**
     * Automated cron schedule checking every 5 minutes by default
     * Configurable in application.properties: app.alerts.overdue-cron
     */
    @Scheduled(cron = "${app.alerts.overdue-cron:0 */5 * * * *}")
    public void scheduledOverdueAlertScan() {
        log.info("⏰ [CRON START] Running automated Overdue Follow-up scan at {}", LocalDateTime.now());
        try {
            OverdueAlertReportDto report = checkAndDispatchOverdueAlerts();
            log.info("⏰ [CRON COMPLETE] Overdue Scan completed: {} followups found, {} notifications sent.",
                    report.getOverdueFollowupsFound(), report.getNotificationsDispatched());
        } catch (Exception e) {
            log.error("❌ Error running scheduled overdue alert scan: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public OverdueAlertReportDto checkAndDispatchOverdueAlerts() {
        LocalDateTime now = LocalDateTime.now();
        List<Followup> unalertedOverdues = followupRepository.findUnAlertedOverdueFollowups(now);
        List<Lead> impendingLeads = leadRepository.findImpendingFollowups(now);

        int notificationsSent = 0;
        int timelineEvents = 0;
        List<String> summaries = new ArrayList<>();

        for (Followup followup : unalertedOverdues) {
            Lead lead = followup.getLead();
            Employee employee = followup.getAssignedEmployee();

            // Mark followup as OVERDUE and alert sent
            followup.setStatus(FollowupStatus.OVERDUE);
            followup.setOverdueAlertSent(true);
            followupRepository.save(followup);

            String customerName = (lead != null) ? lead.getCustomerName() : "Unknown Customer";
            String companyName = (lead != null && lead.getCompanyName() != null) ? " (" + lead.getCompanyName() + ")" : "";
            String alertMsg = String.format("🚨 OVERDUE ALERT: %s follow-up with %s%s was due at %s and is now overdue!",
                    followup.getFollowupType(), customerName, companyName, followup.getFollowupDate());

            // 1. Dispatch in-app notification to assigned employee
            if (employee != null) {
                notificationService.createNotification(NotificationRequestDto.builder()
                        .employeeId(employee.getId())
                        .title("⚠️ Overdue Follow-up Alert")
                        .message(alertMsg)
                        .type(NotificationType.IN_APP)
                        .referenceType("FOLLOWUP")
                        .referenceId(followup.getId())
                        .build());
                notificationsSent++;
            }

            // 2. Log in Activity Timeline
            if (lead != null) {
                timelineService.logActivity(
                        lead.getId(),
                        employee != null ? employee.getId() : null,
                        ActivityType.FOLLOWUP_OVERDUE,
                        "Overdue Follow-up Alert: " + followup.getFollowupType(),
                        alertMsg,
                        FollowupStatus.PENDING.name(),
                        FollowupStatus.OVERDUE.name()
                );
                timelineEvents++;
            }

            summaries.add(String.format("Followup #%d for Lead '%s' due at %s marked OVERDUE and alerted.",
                    followup.getId(), customerName, followup.getFollowupDate()));
        }

        return OverdueAlertReportDto.builder()
                .scanTimestamp(now)
                .totalPendingChecked(unalertedOverdues.size() + impendingLeads.size())
                .overdueFollowupsFound(unalertedOverdues.size())
                .overdueLeadsFound(impendingLeads.size())
                .notificationsDispatched(notificationsSent)
                .timelineEventsLogged(timelineEvents)
                .alertSummaries(summaries)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowupResponseDto> getActiveOverdueFollowups() {
        LocalDateTime now = LocalDateTime.now();
        List<Followup> overdues = followupRepository.findByStatusAndFollowupDateBefore(FollowupStatus.PENDING, now);
        List<Followup> markedOverdues = followupRepository.findByStatus(FollowupStatus.OVERDUE);

        List<Followup> combined = new ArrayList<>(overdues);
        for (Followup f : markedOverdues) {
            if (!combined.contains(f)) {
                combined.add(f);
            }
        }

        return combined.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private FollowupResponseDto mapToDto(Followup f) {
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
                .isOverdue(true)
                .build();
    }
}
