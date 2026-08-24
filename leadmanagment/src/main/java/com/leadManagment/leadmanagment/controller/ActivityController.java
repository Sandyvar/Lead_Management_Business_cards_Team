package com.leadManagment.leadmanagment.controller;

import com.leadManagment.leadmanagment.model.Activity;
import com.leadManagment.leadmanagment.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public Activity createActivity(
            @RequestParam Long leadId,
            @RequestParam Long employeeId,
            @RequestParam String activityType,
            @RequestParam String description) {

        return activityService.createActivity(
                leadId,
                employeeId,
                activityType,
                description
        );
    }

    @GetMapping("/lead/{leadId}")
    public List<Activity> getActivitiesByLead(
            @PathVariable Long leadId) {

        return activityService.getActivitiesByLead(leadId);
    }
}