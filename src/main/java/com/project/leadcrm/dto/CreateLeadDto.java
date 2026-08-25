package com.project.leadcrm.dto;

import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.model.enums.LeadSource;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.model.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLeadDto {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String companyName;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    private String whatsapp;
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private String requirement;

    private LeadSource leadSource;
    private LeadCategory leadCategory;
    private LeadStatus leadStatus;
    private Priority priority;
    private Double leadValue;
    private Long assignedEmployeeId;
    private LocalDateTime nextFollowup;
    private String notes;
    private Long companyId;
}
