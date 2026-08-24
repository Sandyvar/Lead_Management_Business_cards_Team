package com.project.leadcrm.dto;

import com.project.leadcrm.model.UserRole;
import com.project.leadcrm.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        UserRole role,
        UserStatus status
) {
}
