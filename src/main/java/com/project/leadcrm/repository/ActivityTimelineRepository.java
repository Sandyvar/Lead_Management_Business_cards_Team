package com.project.leadcrm.repository;

import com.project.leadcrm.model.ActivityTimeline;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityTimelineRepository extends JpaRepository<ActivityTimeline, Long> {

    List<ActivityTimeline> findByLeadIdOrderByTimestampDesc(Long leadId);

    List<ActivityTimeline> findByEmployeeIdOrderByTimestampDesc(Long employeeId);

    @Query("SELECT a FROM ActivityTimeline a ORDER BY a.timestamp DESC")
    List<ActivityTimeline> findRecentActivities(Pageable pageable);
}
