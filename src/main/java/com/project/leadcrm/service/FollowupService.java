package com.project.leadcrm.service;

import com.project.leadcrm.dto.FollowupResponseDto;
import com.project.leadcrm.dto.ScheduleFollowupDto;

import java.util.List;

public interface FollowupService {

    FollowupResponseDto scheduleFollowup(ScheduleFollowupDto dto);

    FollowupResponseDto completeFollowup(Long followupId, String outcomeNotes);

    FollowupResponseDto getFollowupById(Long id);

    List<FollowupResponseDto> getFollowupsForToday();

    List<FollowupResponseDto> getOverdueFollowups();

    List<FollowupResponseDto> getFollowupsByLeadId(Long leadId);

    List<FollowupResponseDto> getFollowupsByEmployeeId(Long employeeId);
}
