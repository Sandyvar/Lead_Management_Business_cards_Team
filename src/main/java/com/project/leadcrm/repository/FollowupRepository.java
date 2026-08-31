package com.project.leadcrm.repository;

import com.project.leadcrm.model.Followup;
import com.project.leadcrm.model.enums.FollowupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FollowupRepository extends JpaRepository<Followup, Long> {

    List<Followup> findByStatus(FollowupStatus status);

    List<Followup> findByAssignedEmployeeId(Long employeeId);

    List<Followup> findByAssignedEmployeeIdAndStatus(Long employeeId, FollowupStatus status);

    List<Followup> findByLeadId(Long leadId);

    List<Followup> findByLeadIdOrderByFollowupDateDesc(Long leadId);

    List<Followup> findByFollowupDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT f FROM Followup f WHERE f.status = :status AND f.followupDate < :dateTime")
    List<Followup> findByStatusAndFollowupDateBefore(
            @Param("status") FollowupStatus status,
            @Param("dateTime") LocalDateTime dateTime
    );

    @Query("SELECT f FROM Followup f WHERE f.status = 'PENDING' AND f.followupDate < :dateTime AND f.overdueAlertSent = false")
    List<Followup> findUnAlertedOverdueFollowups(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT f FROM Followup f WHERE f.followupDate >= :startOfDay AND f.followupDate <= :endOfDay ORDER BY f.followupDate ASC")
    List<Followup> findTodayFollowups(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
