package com.project.leadcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNoteRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotBlank(message = "Note content is required")
    private String note;
}
