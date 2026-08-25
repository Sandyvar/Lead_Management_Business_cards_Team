package com.project.leadcrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignLeadDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private Long assignedByEmployeeId;

    private String note;
}
