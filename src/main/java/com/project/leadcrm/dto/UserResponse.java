package com.project.leadcrm.dto;

import com.project.leadcrm.model.UserRole;
import com.project.leadcrm.model.UserStatus;
import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
