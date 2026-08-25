package com.project.leadcrm.dto;

import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.model.enums.LeadSource;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.model.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadDto {

    private Long id;
    private String customerName;
    private String companyName;
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
    private Integer leadScore;
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    private LocalDateTime createdDate;
    private LocalDateTime lastContactDate;
    private LocalDateTime nextFollowup;
    private String notes;
    private Long companyId;

    public static LeadDto fromEntity(Lead entity) {
        return LeadDto.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .companyName(entity.getCompanyName())
                .mobile(entity.getMobile())
                .whatsapp(entity.getWhatsapp())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .requirement(entity.getRequirement())
                .leadSource(entity.getLeadSource())
                .leadCategory(entity.getLeadCategory())
                .leadStatus(entity.getLeadStatus())
                .priority(entity.getPriority())
                .leadValue(entity.getLeadValue())
                .leadScore(entity.getLeadScore())
                .assignedEmployeeId(entity.getAssignedEmployee() != null ? entity.getAssignedEmployee().getId() : null)
                .assignedEmployeeName(entity.getAssignedEmployee() != null ? entity.getAssignedEmployee().getName() : null)
                .createdDate(entity.getCreatedDate())
                .lastContactDate(entity.getLastContactDate())
                .nextFollowup(entity.getNextFollowup())
                .notes(entity.getNotes())
                .companyId(entity.getCompanyId())
                .build();
    }
}
