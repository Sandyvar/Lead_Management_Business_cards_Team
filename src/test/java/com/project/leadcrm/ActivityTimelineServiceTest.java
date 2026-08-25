package com.project.leadcrm;

import com.project.leadcrm.dto.ActivityTimelineDto;
import com.project.leadcrm.model.ActivityTimeline;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.enums.ActivityType;
import com.project.leadcrm.model.enums.Role;
import com.project.leadcrm.repository.ActivityTimelineRepository;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.service.impl.ActivityTimelineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityTimelineServiceTest {

    @Mock
    private ActivityTimelineRepository timelineRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ActivityTimelineServiceImpl timelineService;

    private Employee mockEmployee;
    private ActivityTimeline mockTimeline;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(1L)
                .name("Vaibhav")
                .role(Role.SALES_EXECUTIVE)
                .build();

        mockTimeline = ActivityTimeline.builder()
                .id(501L)
                .leadId(10L)
                .employee(mockEmployee)
                .activityType(ActivityType.STATUS_CHANGED)
                .title("Status Changed to CONTACTED")
                .details("Client called and discussed requirements")
                .oldValue("NEW")
                .newValue("CONTACTED")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Day 2-3: Log new activity event to timeline")
    void testLogActivity() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(timelineRepository.save(any(ActivityTimeline.class))).thenReturn(mockTimeline);

        ActivityTimelineDto result = timelineService.logActivity(
                10L, 1L, ActivityType.STATUS_CHANGED,
                "Status Changed to CONTACTED",
                "Client called and discussed requirements",
                "NEW", "CONTACTED"
        );

        assertNotNull(result);
        assertEquals(10L, result.getLeadId());
        assertEquals("Vaibhav", result.getEmployeeName());
        assertEquals(ActivityType.STATUS_CHANGED, result.getActivityType());
        assertEquals("NEW", result.getOldValue());
        assertEquals("CONTACTED", result.getNewValue());
        verify(timelineRepository, times(1)).save(any(ActivityTimeline.class));
    }

    @Test
    @DisplayName("Day 2-3: Retrieve chronological timeline for a lead")
    void testGetTimelineForLead() {
        when(timelineRepository.findByLeadIdOrderByTimestampDesc(10L)).thenReturn(Arrays.asList(mockTimeline));

        List<ActivityTimelineDto> list = timelineService.getTimelineForLead(10L);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Status Changed to CONTACTED", list.get(0).getTitle());
        verify(timelineRepository, times(1)).findByLeadIdOrderByTimestampDesc(10L);
    }

    @Test
    @DisplayName("Day 2-3: Add internal note to lead timeline")
    void testAddNote() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(timelineRepository.save(any(ActivityTimeline.class))).thenAnswer(invocation -> {
            ActivityTimeline arg = invocation.getArgument(0);
            arg.setId(502L);
            return arg;
        });

        ActivityTimelineDto result = timelineService.addNote(10L, 1L, "Follow up call required on Monday");

        assertNotNull(result);
        assertEquals(ActivityType.NOTE_ADDED, result.getActivityType());
        assertEquals("Follow up call required on Monday", result.getDetails());
    }
}
