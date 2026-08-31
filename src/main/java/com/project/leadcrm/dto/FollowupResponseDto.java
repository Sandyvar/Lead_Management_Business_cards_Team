package com.project.leadcrm.dto;

import com.project.leadcrm.model.enums.FollowupStatus;
import com.project.leadcrm.model.enums.FollowupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowupResponseDto {

    private Long id;
    private Long leadId;
    private String customerName;
    private String companyName;
    private String leadMobile;
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    private LocalDateTime followupDate;
    private FollowupType followupType;
    private FollowupStatus status;
    private String notes;
    private Boolean reminderSent;
    private Boolean overdueAlertSent;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private Boolean isOverdue;
}
