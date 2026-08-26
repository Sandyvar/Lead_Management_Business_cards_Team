package com.project.leadcrm.dto;

import com.project.leadcrm.model.enums.LeadSource;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaptureLeadRequestDto {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String companyName;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    private String email;

    private String city;
    private String state;
    private String country;

    private String requirement;

    private LeadSource source;

    private Long cardId;

    private Long assignedEmployeeId;

    private Long companyId;

    @Builder.Default
    private boolean isDirectEnquiryForm = true;

    @Builder.Default
    private boolean hasWhatsAppClick = false;

    @Builder.Default
    private boolean hasPhoneCallClick = false;

    @Builder.Default
    private boolean isQrScan = false;

    @Builder.Default
    private boolean hasWebsiteVisit = false;

    @Builder.Default
    private boolean isProposalRequested = false;

    @Builder.Default
    private boolean isMeetingRequested = false;
}
