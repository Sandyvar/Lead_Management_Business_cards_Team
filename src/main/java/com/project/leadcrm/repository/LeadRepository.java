package com.project.leadcrm.repository;

import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.model.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByLeadStatus(LeadStatus leadStatus);
    List<Lead> findByAssignedEmployeeId(Long employeeId);
    List<Lead> findByAssignedEmployeeIdAndLeadStatus(Long employeeId, LeadStatus status);
    List<Lead> findByPriority(Priority priority);
    List<Lead> findByLeadCategory(LeadCategory category);
    List<Lead> findByCompanyId(Long companyId);

    @Query("SELECT l FROM Lead l WHERE l.nextFollowup <= :deadline AND l.leadStatus NOT IN ('WON', 'LOST')")
    List<Lead> findImpendingFollowups(LocalDateTime deadline);

    @Query("SELECT l.leadStatus, COUNT(l) FROM Lead l GROUP BY l.leadStatus")
    List<Object[]> countLeadsByStatus();
}
