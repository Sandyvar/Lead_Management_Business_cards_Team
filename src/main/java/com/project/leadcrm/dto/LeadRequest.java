package com.project.leadcrm.dto;

import com.project.leadcrm.model.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @Size(max = 40) String phone,
        @Size(max = 120) String company,
        LeadStatus status,
        @Size(max = 1000) String notes
) {
}
