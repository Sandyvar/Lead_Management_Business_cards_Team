package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.NotificationRequestDto;
import com.project.leadcrm.dto.NotificationResponseDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.Notification;
import com.project.leadcrm.model.enums.NotificationType;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.repository.NotificationRepository;
import com.project.leadcrm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public NotificationResponseDto sendNotification(NotificationRequestDto requestDto) {
        log.info("Processing notification for employee ID: {}", requestDto.getEmployeeId());

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + requestDto.getEmployeeId()));

        NotificationType type = requestDto.getType() != null ? requestDto.getType() : NotificationType.IN_APP;

        Notification notification = Notification.builder()
                .employee(employee)
                .title(requestDto.getTitle())
                .message(requestDto.getMessage())
                .type(type)
                .isRead(false)
                .referenceType(requestDto.getReferenceType())
                .referenceId(requestDto.getReferenceId())
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Saved notification with ID: {}", saved.getId());

        // Asynchronously dispatch external notifications (e.g. Email / SMS simulation)
        if (type == NotificationType.EMAIL && employee.getEmail() != null) {
            sendEmailNotificationAsync(employee.getEmail(), requestDto.getTitle(), requestDto.getMessage());
        }

        return NotificationResponseDto.fromEntity(saved);
    }

    @Async
    public void sendEmailNotificationAsync(String toEmail, String subject, String body) {
        try {
            if (mailSender != null) {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(toEmail);
                mailMessage.setSubject(subject);
                mailMessage.setText(body);
                mailSender.send(mailMessage);
                log.info("Email notification sent successfully to {}", toEmail);
            } else {
                log.info("[Email Simulation] To: {} | Subject: {} | Body: {}", toEmail, subject, body);
            }
        } catch (Exception ex) {
            log.warn("Failed to dispatch email notification: {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByEmployee(Long employeeId) {
        return notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotificationsByEmployee(Long employeeId) {
        return notificationRepository.findByEmployeeIdAndIsReadFalseOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long employeeId) {
        return notificationRepository.countByEmployeeIdAndIsReadFalse(employeeId);
    }

    @Override
    @Transactional
    public NotificationResponseDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        Notification updated = notificationRepository.save(notification);

        return NotificationResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public int markAllAsRead(Long employeeId) {
        return notificationRepository.markAllAsReadByEmployeeId(employeeId, LocalDateTime.now());
    }
}
