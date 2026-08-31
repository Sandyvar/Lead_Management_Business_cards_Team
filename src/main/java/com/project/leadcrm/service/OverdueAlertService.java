package com.project.leadcrm.service;

import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.OverdueAlertReportDto;

import java.util.List;

public interface OverdueAlertService {

    OverdueAlertReportDto checkAndDispatchOverdueAlerts();

    List<FollowupResponseDto> getActiveOverdueFollowups();
}
