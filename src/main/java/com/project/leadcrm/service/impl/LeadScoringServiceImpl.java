package com.project.leadcrm.service.impl;

import com.project.leadcrm.dto.CaptureLeadRequestDto;
import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.service.LeadScoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LeadScoringServiceImpl implements LeadScoringService {

    @Value("${app.lead.scoring.qr-scan-points:5}")
    private int qrScanPoints;

    @Value("${app.lead.scoring.website-visit-points:5}")
    private int websiteVisitPoints;

    @Value("${app.lead.scoring.whatsapp-click-points:10}")
    private int whatsappClickPoints;

    @Value("${app.lead.scoring.call-click-points:10}")
    private int callClickPoints;

    @Value("${app.lead.scoring.enquiry-points:20}")
    private int enquiryPoints;

    @Value("${app.lead.scoring.proposal-request-points:30}")
    private int proposalRequestPoints;

    @Value("${app.lead.scoring.meeting-points:40}")
    private int meetingPoints;

    @Override
    public int calculateScore(CaptureLeadRequestDto request) {
        int score = 0;

        if (request.isQrScan()) {
            score += qrScanPoints;
        }
        if (request.isHasWebsiteVisit()) {
            score += websiteVisitPoints;
        }
        if (request.isHasWhatsAppClick()) {
            score += whatsappClickPoints;
        }
        if (request.isHasPhoneCallClick()) {
            score += callClickPoints;
        }
        if (request.isDirectEnquiryForm()) {
            score += enquiryPoints;
        }
        if (request.isProposalRequested()) {
            score += proposalRequestPoints;
        }
        if (request.isMeetingRequested()) {
            score += meetingPoints;
        }

        log.debug("Calculated lead score: {} for request from {}", score, request.getCustomerName());
        return score;
    }

    @Override
    public int calculateScoreForAction(String actionType) {
        if (actionType == null) {
            return 0;
        }
        return switch (actionType.toUpperCase()) {
            case "QR_SCAN", "CARD_VISIT" -> qrScanPoints;
            case "WEBSITE_VISIT" -> websiteVisitPoints;
            case "WHATSAPP_CLICK" -> whatsappClickPoints;
            case "CALL_CLICK", "PHONE_CLICK" -> callClickPoints;
            case "ENQUIRY_FORM", "DIRECT_ENQUIRY" -> enquiryPoints;
            case "PROPOSAL_REQUEST" -> proposalRequestPoints;
            case "MEETING_REQUEST", "DEMO_REQUEST" -> meetingPoints;
            default -> 0;
        };
    }

    @Override
    public LeadCategory evaluateCategory(int totalScore) {
        if (totalScore >= 36) {
            return LeadCategory.HOT;
        } else if (totalScore >= 16) {
            return LeadCategory.WARM;
        } else {
            return LeadCategory.COLD;
        }
    }
}
