package com.project.leadcrm.controller;

import com.project.leadcrm.dto.ApiResponse;
import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.NotificationResponseDto;
import com.project.leadcrm.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Service", description = "Endpoints for alerts, email/in-app notifications, and unread counts (Day 1 Task)")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send / trigger a new notification to an employee")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> sendNotification(@Valid @RequestBody NotificationRequestDto requestDto) {
        NotificationResponseDto result = notificationService.sendNotification(requestDto);
        return new ResponseEntity<>(ApiResponse.success("Notification sent successfully", result), HttpStatus.CREATED);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get all notifications for an employee (ordered newest to oldest)")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getEmployeeNotifications(@PathVariable Long employeeId) {
        List<NotificationResponseDto> list = notificationService.getNotificationsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", list));
    }

    @GetMapping("/employee/{employeeId}/unread")
    @Operation(summary = "Get only unread notifications for an employee")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUnreadNotifications(@PathVariable Long employeeId) {
        List<NotificationResponseDto> list = notificationService.getUnreadNotificationsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved successfully", list));
    }

    @GetMapping("/employee/{employeeId}/unread-count")
    @Operation(summary = "Get unread notification count badge for an employee")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long employeeId) {
        long count = notificationService.getUnreadCount(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markAsRead(@PathVariable Long id) {
        NotificationResponseDto result = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", result));
    }

    @PutMapping("/employee/{employeeId}/read-all")
    @Operation(summary = "Mark all notifications for an employee as read")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(@PathVariable Long employeeId) {
        int updatedCount = notificationService.markAllAsRead(employeeId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", updatedCount));
    }
}
