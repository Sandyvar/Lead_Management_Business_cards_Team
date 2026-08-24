package com.leadManagment.leadmanagment.service;

import com.leadManagment.leadmanagment.model.Notification;
import com.leadManagment.leadmanagment.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Send a new notification
    public Notification sendNotification(Long userId, String type, String message) {
        Notification notification = new Notification(userId, type, message);
        notification.setStatus("SENT");
        notification.setSentAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    // Get all notifications for a user
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Get pending notifications
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatus("PENDING");
    }

    // Update notification status
    public Notification updateNotificationStatus(Long id, String status) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setStatus(status);
            return notificationRepository.save(notification);
        }
        return null;
    }
}