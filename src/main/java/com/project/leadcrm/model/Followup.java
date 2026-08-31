package com.project.leadcrm.model;

import com.project.leadcrm.model.enums.FollowupStatus;
import com.project.leadcrm.model.enums.FollowupType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "followups", indexes = {
    @Index(name = "idx_followup_lead", columnList = "lead_id"),
    @Index(name = "idx_followup_employee", columnList = "employee_id"),
    @Index(name = "idx_followup_date", columnList = "followup_date"),
    @Index(name = "idx_followup_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Followup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "followup_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee assignedEmployee;

    @Column(name = "followup_date", nullable = false)
    private LocalDateTime followupDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "followup_type", nullable = false)
    @Builder.Default
    private FollowupType followupType = FollowupType.CALL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FollowupStatus status = FollowupStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "reminder_sent", nullable = false)
    @Builder.Default
    private Boolean reminderSent = false;

    @Column(name = "overdue_alert_sent", nullable = false)
    @Builder.Default
    private Boolean overdueAlertSent = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = FollowupStatus.PENDING;
        }
        if (reminderSent == null) {
            reminderSent = false;
        }
        if (overdueAlertSent == null) {
            overdueAlertSent = false;
        }
    }
}
