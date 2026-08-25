package com.project.leadcrm.dto;

import com.project.leadcrm.model.ActivityTimeline;
import com.project.leadcrm.model.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityTimelineDto {

    private Long id;
    private Long leadId;
    private Long employeeId;
    private String employeeName;
    private ActivityType activityType;
    private String title;
    private String details;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;

    public static ActivityTimelineDto fromEntity(ActivityTimeline entity) {
        return ActivityTimelineDto.builder()
                .id(entity.getId())
                .leadId(entity.getLeadId())
                .employeeId(entity.getEmployee() != null ? entity.getEmployee().getId() : null)
                .employeeName(entity.getEmployee() != null ? entity.getEmployee().getName() : "System")
                .activityType(entity.getActivityType())
                .title(entity.getTitle())
                .details(entity.getDetails())
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
