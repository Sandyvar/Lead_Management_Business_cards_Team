package com.project.leadcrm.repository;

import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByRole(Role role);
    List<Employee> findByDepartment(String department);
    List<Employee> findByCompanyId(Long companyId);
}
