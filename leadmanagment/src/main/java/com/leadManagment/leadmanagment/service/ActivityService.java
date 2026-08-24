package com.leadManagment.leadmanagment.service;

import com.leadManagment.leadmanagment.model.Activity;
import com.leadManagment.leadmanagment.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public Activity createActivity(Long leadId,
                                   Long employeeId,
                                   String activityType,
                                   String description) {

        Activity activity = new Activity(
                leadId,
                employeeId,
                activityType,
                description
        );

        return activityRepository.save(activity);
    }

    public List<Activity> getActivitiesByLead(Long leadId) {
        return activityRepository
                .findByLeadIdOrderByCreatedAtDesc(leadId);
    }
}