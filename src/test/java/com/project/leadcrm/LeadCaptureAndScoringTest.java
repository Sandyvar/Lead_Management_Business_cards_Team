package com.project.leadcrm;

import com.project.leadcrm.dto.CaptureLeadRequestDto;
import com.project.leadcrm.dto.LeadDto;
import com.project.leadcrm.dto.PipelineBoardDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.*;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.LeadRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import com.project.leadcrm.service.NotificationService;
import com.project.leadcrm.service.impl.LeadCaptureServiceImpl;
import com.project.leadcrm.service.impl.LeadScoringServiceImpl;
import com.project.leadcrm.service.impl.LeadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeadCaptureAndScoringTest {

    @Spy
    private LeadScoringServiceImpl scoringService;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ActivityTimelineService timelineService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LeadCaptureServiceImpl leadCaptureService;

    @InjectMocks
    private LeadServiceImpl leadService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        // Initialize default scoring points in spy
        ReflectionTestUtils.setField(scoringService, "qrScanPoints", 5);
        ReflectionTestUtils.setField(scoringService, "websiteVisitPoints", 5);
        ReflectionTestUtils.setField(scoringService, "whatsappClickPoints", 10);
        ReflectionTestUtils.setField(scoringService, "callClickPoints", 10);
        ReflectionTestUtils.setField(scoringService, "enquiryPoints", 20);
        ReflectionTestUtils.setField(scoringService, "proposalRequestPoints", 30);
        ReflectionTestUtils.setField(scoringService, "meetingPoints", 40);

        mockEmployee = Employee.builder()
                .id(1L)
                .name("Vaibhav")
                .role(Role.SALES_EXECUTIVE)
                .build();
    }

    @Test
    @DisplayName("Should correctly calculate points and classify as WARM (35 pts)")
    void testScoringWarmClassification() {
        CaptureLeadRequestDto request = CaptureLeadRequestDto.builder()
                .customerName("Rahul Verma")
                .mobile("+919876500001")
                .isQrScan(true)              // +5
                .hasWhatsAppClick(true)      // +10
                .isDirectEnquiryForm(true)   // +20
                .build();

        int score = scoringService.calculateScore(request);
        assertEquals(35, score);

        LeadCategory category = scoringService.evaluateCategory(score);
        assertEquals(LeadCategory.WARM, category);
    }

    @Test
    @DisplayName("Should correctly classify as HOT when meeting/proposal requested (70 pts)")
    void testScoringHotClassification() {
        CaptureLeadRequestDto request = CaptureLeadRequestDto.builder()
                .customerName("Enterprise Client")
                .mobile("+919876500002")
                .isProposalRequested(true)   // +30
                .isMeetingRequested(true)    // +40
                .isDirectEnquiryForm(false)
                .build();

        int score = scoringService.calculateScore(request);
        assertEquals(70, score);

        LeadCategory category = scoringService.evaluateCategory(score);
        assertEquals(LeadCategory.HOT, category);
    }

    @Test
    @DisplayName("Should capture a new inbound lead, calculate score and notify assignee")
    void testCaptureNewLead() {
        CaptureLeadRequestDto request = CaptureLeadRequestDto.builder()
                .customerName("Tech Innovators")
                .companyName("Innovate Corp")
                .mobile("+919999888877")
                .email("info@innovate.com")
                .requirement("Enterprise CRM license")
                .isQrScan(true)
                .isDirectEnquiryForm(true)
                .assignedEmployeeId(1L)
                .build();

        when(leadRepository.findFirstByMobileOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        Lead savedLead = Lead.builder()
                .id(100L)
                .customerName("Tech Innovators")
                .companyName("Innovate Corp")
                .mobile("+919999888877")
                .email("info@innovate.com")
                .leadSource(LeadSource.QR_CODE)
                .leadCategory(LeadCategory.WARM)
                .leadStatus(LeadStatus.NEW)
                .priority(Priority.MEDIUM)
                .leadScore(25)
                .assignedEmployee(mockEmployee)
                .createdDate(LocalDateTime.now())
                .build();

        when(leadRepository.save(any(Lead.class))).thenReturn(savedLead);

        LeadDto result = leadCaptureService.captureLead(request);

        assertNotNull(result);
        assertEquals("Tech Innovators", result.getCustomerName());
        assertEquals(LeadCategory.WARM, result.getLeadCategory());
        verify(timelineService, times(1)).logActivity(eq(100L), eq(1L), eq(ActivityType.LEAD_CREATED), anyString(), anyString(), isNull(), eq("NEW"));
        verify(notificationService, times(1)).sendNotification(any());
    }

    @Test
    @DisplayName("Should re-engage and accumulate points for an existing lead")
    void testReengageExistingLead() {
        Lead existingLead = Lead.builder()
                .id(200L)
                .customerName("John Doe")
                .mobile("+919876543210")
                .leadScore(10)
                .leadCategory(LeadCategory.COLD)
                .leadStatus(LeadStatus.CONTACTED)
                .assignedEmployee(mockEmployee)
                .build();

        when(leadRepository.findFirstByMobileOrEmail("+919876543210", null))
                .thenReturn(Optional.of(existingLead));

        CaptureLeadRequestDto reEngagement = CaptureLeadRequestDto.builder()
                .customerName("John Doe")
                .mobile("+919876543210")
                .isProposalRequested(true) // +30 pts -> total = 40 (HOT)
                .isDirectEnquiryForm(false)
                .build();

        when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadCaptureService.captureLead(reEngagement);

        assertNotNull(result);
        assertEquals(40, result.getLeadScore());
        assertEquals(LeadCategory.HOT, result.getLeadCategory());
        verify(timelineService, times(1)).logActivity(eq(200L), eq(1L), eq(ActivityType.NOTE_ADDED), anyString(), anyString(), eq("COLD"), eq("HOT"));
        verify(notificationService, times(1)).sendNotification(any());
    }

    @Test
    @DisplayName("Should generate Pipeline Kanban board grouped by stage with correct counts and total values")
    void testGetPipelineBoard() {
        Lead l1 = Lead.builder().id(1L).customerName("Lead 1").leadStatus(LeadStatus.NEW).leadValue(10000.0).build();
        Lead l2 = Lead.builder().id(2L).customerName("Lead 2").leadStatus(LeadStatus.NEW).leadValue(20000.0).build();
        Lead l3 = Lead.builder().id(3L).customerName("Lead 3").leadStatus(LeadStatus.WON).leadValue(50000.0).build();

        when(leadRepository.findAll()).thenReturn(Arrays.asList(l1, l2, l3));

        PipelineBoardDto board = leadService.getPipelineBoard();

        assertNotNull(board);
        assertEquals(3, board.getTotalLeads());
        assertEquals(80000.0, board.getTotalPipelineValue());
        assertEquals(LeadStatus.values().length, board.getStages().size());

        // Check NEW stage
        var newStage = board.getStages().stream()
                .filter(s -> s.getStage() == LeadStatus.NEW)
                .findFirst()
                .orElse(null);
        assertNotNull(newStage);
        assertEquals(2, newStage.getLeadCount());
        assertEquals(30000.0, newStage.getTotalValue());

        // Check WON stage
        var wonStage = board.getStages().stream()
                .filter(s -> s.getStage() == LeadStatus.WON)
                .findFirst()
                .orElse(null);
        assertNotNull(wonStage);
        assertEquals(1, wonStage.getLeadCount());
        assertEquals(50000.0, wonStage.getTotalValue());
    }
}
