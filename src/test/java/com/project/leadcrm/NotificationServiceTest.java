package com.project.leadcrm;

import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.NotificationResponseDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Notification;
import com.project.leadcrm.model.enums.NotificationType;
import com.project.leadcrm.model.enums.Role;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.NotificationRepository;
import com.project.leadcrm.service.NotificationService;
import com.project.leadcrm.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Employee mockEmployee;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(1L)
                .name("Vaibhav")
                .email("vaibhav@leadcrm.com")
                .role(Role.SALES_EXECUTIVE)
                .build();

        mockNotification = Notification.builder()
                .id(101L)
                .employee(mockEmployee)
                .title("New Lead Assigned")
                .message("Lead Acme Corp assigned to you")
                .type(NotificationType.IN_APP)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Day 1: Successfully send notification to employee")
    void testSendNotification_Success() {
        NotificationRequestDto requestDto = NotificationRequestDto.builder()
                .employeeId(1L)
                .title("New Lead Assigned")
                .message("Lead Acme Corp assigned to you")
                .type(NotificationType.IN_APP)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        NotificationResponseDto response = notificationService.sendNotification(requestDto);

        assertNotNull(response);
        assertEquals("New Lead Assigned", response.getTitle());
        assertEquals(1L, response.getEmployeeId());
        assertFalse(response.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Day 1: Mark notification as read")
    void testMarkAsRead() {
        when(notificationRepository.findById(101L)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDto response = notificationService.markAsRead(101L);

        assertNotNull(response);
        assertTrue(response.getIsRead());
        assertNotNull(response.getReadAt());
        verify(notificationRepository, times(1)).save(mockNotification);
    }

    @Test
    @DisplayName("Day 1: Get unread count for badge")
    void testGetUnreadCount() {
        when(notificationRepository.countByEmployeeIdAndIsReadFalse(1L)).thenReturn(3L);

        long unreadCount = notificationService.getUnreadCount(1L);

        assertEquals(3L, unreadCount);
        verify(notificationRepository, times(1)).countByEmployeeIdAndIsReadFalse(1L);
    }
}
