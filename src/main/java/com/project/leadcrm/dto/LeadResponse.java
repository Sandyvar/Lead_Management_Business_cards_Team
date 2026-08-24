package com.project.leadcrm.dto;

import com.project.leadcrm.model.LeadStatus;
import java.time.Instant;

public record LeadResponse(
        Long id,
        String name,
        String email,
        String phone,
        String company,
        LeadStatus status,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
}
