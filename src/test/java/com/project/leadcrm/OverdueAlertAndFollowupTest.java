package com.project.leadcrm;

import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.OverdueAlertReportDto;
import com.project.leadcrm.dto.ScheduleFollowupDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Followup;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.*;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.FollowupRepository;
import com.project.leadcrm.repository.LeadRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import com.project.leadcrm.service.NotificationService;
import com.project.leadcrm.service.impl.FollowupServiceImpl;
import com.project.leadcrm.service.impl.OverdueAlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OverdueAlertAndFollowupTest {

    @Mock
    private FollowupRepository followupRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ActivityTimelineService timelineService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowupServiceImpl followupService;

    @InjectMocks
    private OverdueAlertServiceImpl overdueAlertService;

    private Employee mockEmployee;
    private Lead mockLead;
    private Followup mockFollowup;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(1L)
                .name("Vaibhav")
                .email("vaibhav@leadcrm.com")
                .mobile("+919876543210")
                .role(Role.SALES_EXECUTIVE)
                .department("Sales")
                .companyId(1L)
                .build();

        mockLead = Lead.builder()
                .id(100L)
                .customerName("Acme Global Corp")
                .companyName("Acme Industries")
                .mobile("+919811223344")
                .email("sales@acme.com")
                .leadStatus(LeadStatus.FOLLOW_UP)
                .priority(Priority.HIGH)
                .leadCategory(LeadCategory.HOT)
                .assignedEmployee(mockEmployee)
                .companyId(1L)
                .build();

        mockFollowup = Followup.builder()
                .id(500L)
                .lead(mockLead)
                .assignedEmployee(mockEmployee)
                .followupDate(LocalDateTime.now().minusHours(2)) // 2 hours overdue
                .followupType(FollowupType.CALL)
                .status(FollowupStatus.PENDING)
                .notes("Discuss final proposal pricing")
                .reminderSent(false)
                .overdueAlertSent(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("Day 8: Schedule Followup updates Lead nextFollowup, logs Timeline, and notifies Employee")
    void testScheduleFollowup_Success() {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(2);
        ScheduleFollowupDto dto = ScheduleFollowupDto.builder()
                .leadId(100L)
                .employeeId(1L)
                .followupDate(futureTime)
                .followupType(FollowupType.MEETING)
                .notes("Demo presentation on Zoom")
                .build();

        when(leadRepository.findById(100L)).thenReturn(Optional.of(mockLead));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(followupRepository.save(any(Followup.class))).thenAnswer(i -> {
            Followup f = i.getArgument(0);
            f.setId(501L);
            return f;
        });

        FollowupResponseDto response = followupService.scheduleFollowup(dto);

        assertNotNull(response);
        assertEquals(501L, response.getId());
        assertEquals(FollowupType.MEETING, response.getFollowupType());
        assertEquals(FollowupStatus.PENDING, response.getStatus());

        // Verify Lead's nextFollowup was updated
        verify(leadRepository).save(argThat(lead -> futureTime.equals(lead.getNextFollowup())));

        // Verify Timeline activity was logged
        verify(timelineService).logActivity(
                eq(100L),
                eq(1L),
                eq(ActivityType.FOLLOWUP_SCHEDULED),
                contains("Follow-up Scheduled"),
                anyString(),
                isNull(),
                anyString()
        );

        // Verify Notification was sent to employee
        verify(notificationService).createNotification(any(NotificationRequestDto.class));
    }

    @Test
    @DisplayName("Day 8: Complete Followup updates status to COMPLETED and logs completion in Timeline")
    void testCompleteFollowup_Success() {
        when(followupRepository.findById(500L)).thenReturn(Optional.of(mockFollowup));
        when(followupRepository.save(any(Followup.class))).thenAnswer(i -> i.getArgument(0));

        FollowupResponseDto response = followupService.completeFollowup(500L, "Client agreed to sign contract on Monday");

        assertNotNull(response);
        assertEquals(FollowupStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getCompletedAt());

        // Verify Lead's lastContactDate was updated
        verify(leadRepository).save(argThat(lead -> lead.getLastContactDate() != null));

        // Verify Timeline event logged
        verify(timelineService).logActivity(
                eq(100L),
                eq(1L),
                eq(ActivityType.FOLLOWUP_COMPLETED),
                contains("Follow-up Completed"),
                contains("Client agreed to sign contract"),
                eq(FollowupStatus.PENDING.name()),
                eq(FollowupStatus.COMPLETED.name())
        );
    }

    @Test
    @DisplayName("Day 8: Overdue Alert Scanner detects overdue items, dispatches Alert notification, and logs FOLLOWUP_OVERDUE")
    void testOverdueAlertScanner_DetectsOverdueAndDispatchesNotification() {
        when(followupRepository.findUnAlertedOverdueFollowups(any(LocalDateTime.class)))
                .thenReturn(List.of(mockFollowup));
        when(leadRepository.findImpendingFollowups(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(followupRepository.save(any(Followup.class))).thenAnswer(i -> i.getArgument(0));

        OverdueAlertReportDto report = overdueAlertService.checkAndDispatchOverdueAlerts();

        assertNotNull(report);
        assertEquals(1, report.getOverdueFollowupsFound());
        assertEquals(1, report.getNotificationsDispatched());
        assertEquals(1, report.getTimelineEventsLogged());

        // Verify Followup was marked OVERDUE and alertSent = true
        assertTrue(mockFollowup.getOverdueAlertSent());
        assertEquals(FollowupStatus.OVERDUE, mockFollowup.getStatus());

        // Verify High-priority alert notification was dispatched
        ArgumentCaptor<NotificationRequestDto> notifCaptor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationService).createNotification(notifCaptor.capture());
        NotificationRequestDto sentNotif = notifCaptor.getValue();
        assertEquals(1L, sentNotif.getEmployeeId());
        assertTrue(sentNotif.getTitle().contains("Overdue"));
        assertTrue(sentNotif.getMessage().contains("OVERDUE"));

        // Verify ActivityTimeline logged FOLLOWUP_OVERDUE
        verify(timelineService).logActivity(
                eq(100L),
                eq(1L),
                eq(ActivityType.FOLLOWUP_OVERDUE),
                contains("Overdue"),
                anyString(),
                eq(FollowupStatus.PENDING.name()),
                eq(FollowupStatus.OVERDUE.name())
        );
    }

    @Test
    @DisplayName("Day 8: Overdue Alert Scanner suppresses duplicate alerts for already alerted items")
    void testOverdueAlertScanner_PreventsDuplicateAlerts() {
        // First run returns the overdue followup
        when(followupRepository.findUnAlertedOverdueFollowups(any(LocalDateTime.class)))
                .thenReturn(List.of(mockFollowup))
                .thenReturn(Collections.emptyList()); // Second run returns empty list

        when(leadRepository.findImpendingFollowups(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(followupRepository.save(any(Followup.class))).thenAnswer(i -> i.getArgument(0));

        // First scan
        OverdueAlertReportDto firstReport = overdueAlertService.checkAndDispatchOverdueAlerts();
        assertEquals(1, firstReport.getNotificationsDispatched());

        // Second scan (should find 0 unalerted items)
        OverdueAlertReportDto secondReport = overdueAlertService.checkAndDispatchOverdueAlerts();
        assertEquals(0, secondReport.getNotificationsDispatched());

        // Notification created only once
        verify(notificationService, times(1)).createNotification(any(NotificationRequestDto.class));
    }
}
