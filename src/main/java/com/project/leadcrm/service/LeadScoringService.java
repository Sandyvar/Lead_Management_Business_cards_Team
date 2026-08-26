package com.project.leadcrm.service;

import com.project.leadcrm.dto.CaptureLeadRequestDto;
import com.project.leadcrm.model.enums.LeadCategory;

public interface LeadScoringService {

    int calculateScore(CaptureLeadRequestDto request);

    int calculateScoreForAction(String actionType);

    LeadCategory evaluateCategory(int totalScore);
}
