package com.leadManagment.leadmanagment.controller;

import com.leadManagment.leadmanagment.model.Notification;
import com.leadManagment.leadmanagment.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Send notification
    @PostMapping("/send")
    public Notification sendNotification(@RequestParam Long userId,
                                         @RequestParam String type,
                                         @RequestParam String message) {
        return notificationService.sendNotification(userId, type, message);
    }

    // Get notifications for user
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    // Get pending notifications
    @GetMapping("/pending")
    public List<Notification> getPending() {
        return notificationService.getPendingNotifications();
    }

    // Update notification status
    @PutMapping("/{id}/status")
    public Notification updateStatus(@PathVariable Long id, @RequestParam String status) {
        return notificationService.updateNotificationStatus(id, status);
    }
}