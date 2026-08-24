package com.leadManagment.leadmanagment.repository;

import com.leadManagment.leadmanagment.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}