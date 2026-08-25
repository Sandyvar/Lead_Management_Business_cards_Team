package com.project.leadcrm.service;

import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    /**
     * Send or create a new notification (supports In-App, Email, SMS, WhatsApp triggers).
     */
    NotificationResponseDto sendNotification(NotificationRequestDto requestDto);

    /**
     * Retrieve all notifications for a given employee in reverse chronological order.
     */
    List<NotificationResponseDto> getNotificationsByEmployee(Long employeeId);

    /**
     * Retrieve only unread notifications for an employee.
     */
    List<NotificationResponseDto> getUnreadNotificationsByEmployee(Long employeeId);

    /**
     * Get unread notification count (for UI notification badges).
     */
    long getUnreadCount(Long employeeId);

    /**
     * Mark a specific notification as read.
     */
    NotificationResponseDto markAsRead(Long notificationId);

    /**
     * Mark all notifications for an employee as read.
     */
    int markAllAsRead(Long employeeId);
}
