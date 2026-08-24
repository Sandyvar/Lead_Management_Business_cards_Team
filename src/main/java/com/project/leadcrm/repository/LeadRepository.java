package com.project.leadcrm.repository;

import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    Page<Lead> findByStatus(LeadStatus status, Pageable pageable);
}
