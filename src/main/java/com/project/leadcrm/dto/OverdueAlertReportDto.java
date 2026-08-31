package com.project.leadcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdueAlertReportDto {

    private LocalDateTime scanTimestamp;
    private int totalPendingChecked;
    private int overdueFollowupsFound;
    private int overdueLeadsFound;
    private int notificationsDispatched;
    private int timelineEventsLogged;
    
    @Builder.Default
    private List<String> alertSummaries = new ArrayList<>();
}
