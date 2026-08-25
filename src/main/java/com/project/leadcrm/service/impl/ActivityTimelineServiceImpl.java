package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.ActivityTimelineDto;
import com.project.leadcrm.model.ActivityTimeline;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.enums.ActivityType;
import com.project.leadcrm.repository.ActivityTimelineRepository;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.service.ActivityTimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityTimelineServiceImpl implements ActivityTimelineService {

    private final ActivityTimelineRepository timelineRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public ActivityTimelineDto logActivity(Long leadId, Long employeeId, ActivityType activityType,
                                            String title, String details, String oldValue, String newValue) {
        log.info("Logging activity {} for leadId: {}", activityType, leadId);

        Employee employee = null;
        if (employeeId != null) {
            employee = employeeRepository.findById(employeeId).orElse(null);
        }

        ActivityTimeline timeline = ActivityTimeline.builder()
                .leadId(leadId)
                .employee(employee)
                .activityType(activityType)
                .title(title)
                .details(details)
                .oldValue(oldValue)
                .newValue(newValue)
                .timestamp(LocalDateTime.now())
                .build();

        ActivityTimeline saved = timelineRepository.save(timeline);
        return ActivityTimelineDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityTimelineDto> getTimelineForLead(Long leadId) {
        return timelineRepository.findByLeadIdOrderByTimestampDesc(leadId)
                .stream()
                .map(ActivityTimelineDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActivityTimelineDto addNote(Long leadId, Long employeeId, String note) {
        return logActivity(
                leadId,
                employeeId,
                ActivityType.NOTE_ADDED,
                "Internal Note Added",
                note,
                null,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityTimelineDto> getRecentActivities(int limit) {
        int sanitizedLimit = Math.max(1, Math.min(limit, 100));
        return timelineRepository.findRecentActivities(PageRequest.of(0, sanitizedLimit))
                .stream()
                .map(ActivityTimelineDto::fromEntity)
                .collect(Collectors.toList());
    }
}
