package com.project.leadcrm.service;

import com.project.leadcrm.dto.CaptureLeadRequestDto;
import com.project.leadcrm.dto.LeadDto;

public interface LeadCaptureService {

    LeadDto captureLead(CaptureLeadRequestDto request);
}
