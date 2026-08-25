package com.project.leadcrm.service;

import com.project.leadcrm.dto.ActivityTimelineDto;
import com.project.leadcrm.model.enums.ActivityType;

import java.util.List;

public interface ActivityTimelineService {

    /**
     * Records a new activity in the lead's timeline log.
     */
    ActivityTimelineDto logActivity(Long leadId, Long employeeId, ActivityType activityType,
                                    String title, String details, String oldValue, String newValue);

    /**
     * Fetches complete chronological timeline history for a specific lead.
     */
    List<ActivityTimelineDto> getTimelineForLead(Long leadId);

    /**
     * Records a manual note from a salesperson/manager for a lead.
     */
    ActivityTimelineDto addNote(Long leadId, Long employeeId, String note);

    /**
     * Returns most recent activity events across all leads for dashboard live streams.
     */
    List<ActivityTimelineDto> getRecentActivities(int limit);
}
