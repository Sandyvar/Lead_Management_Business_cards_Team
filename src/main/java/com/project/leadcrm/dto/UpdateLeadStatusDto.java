package com.project.leadcrm.dto;

import com.project.leadcrm.model.enums.LeadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLeadStatusDto {

    @NotNull(message = "New lead status is required")
    private LeadStatus status;

    private Long employeeId;

    private String note;
}
