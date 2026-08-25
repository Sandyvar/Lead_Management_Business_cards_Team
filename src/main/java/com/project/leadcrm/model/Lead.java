package com.project.leadcrm.model;

import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.model.enums.LeadSource;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.model.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_id")
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "company_name")
    private String companyName;

    @Column(nullable = false)
    private String mobile;

    private String whatsapp;

    private String email;

    private String address;
    private String city;
    private String state;
    private String country;

    @Column(columnDefinition = "TEXT")
    private String requirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_source")
    private LeadSource leadSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_category")
    private LeadCategory leadCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_status", nullable = false)
    private LeadStatus leadStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(name = "lead_value")
    private Double leadValue;

    @Column(name = "lead_score")
    @Builder.Default
    private Integer leadScore = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_contact_date")
    private LocalDateTime lastContactDate;

    @Column(name = "next_followup")
    private LocalDateTime nextFollowup;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "company_id")
    private Long companyId;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (leadStatus == null) {
            leadStatus = LeadStatus.NEW;
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
        if (leadCategory == null) {
            leadCategory = LeadCategory.COLD;
        }
        if (leadScore == null) {
            leadScore = 0;
        }
    }
}
