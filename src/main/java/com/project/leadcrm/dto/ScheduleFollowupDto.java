package com.project.leadcrm.dto;

import com.project.leadcrm.model.enums.FollowupType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleFollowupDto {

    @NotNull(message = "Lead ID is required")
    private Long leadId;

    private Long employeeId; // Optional; if null, uses lead's assigned employee

    @NotNull(message = "Follow-up date and time is required")
    private LocalDateTime followupDate;

    @Builder.Default
    private FollowupType followupType = FollowupType.CALL;

    private String notes;
}
