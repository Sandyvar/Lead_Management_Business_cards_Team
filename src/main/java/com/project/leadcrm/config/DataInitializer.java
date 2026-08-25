package com.project.leadcrm.config;

import com.project.leadcrm.dto.CreateLeadDto;
import com.project.leadcrm.model.Employee;
import com.project.leadcrm.model.enums.LeadCategory;
import com.project.leadcrm.model.enums.LeadSource;
import com.project.leadcrm.model.enums.LeadStatus;
import com.project.leadcrm.model.enums.Priority;
import com.project.leadcrm.model.enums.Role;
import com.project.leadcrm.repository.EmployeeRepository;
import com.project.leadcrm.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final LeadService leadService;

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            log.info("Initializing demo seed data...");

            // 1. Create Employees
            Employee vaibhav = employeeRepository.save(Employee.builder()
                    .name("Vaibhav")
                    .email("vaibhav@leadcrm.com")
                    .mobile("+919876543210")
                    .role(Role.SALES_EXECUTIVE)
                    .department("Sales")
                    .companyId(1L)
                    .build());

            Employee sandeep = employeeRepository.save(Employee.builder()
                    .name("Sandeep")
                    .email("sandeep@leadcrm.com")
                    .mobile("+919876543211")
                    .role(Role.SALES_MANAGER)
                    .department("Sales")
                    .companyId(1L)
                    .build());

            log.info("Created demo employees: {} (ID: {}), {} (ID: {})",
                    vaibhav.getName(), vaibhav.getId(), sandeep.getName(), sandeep.getId());

            // 2. Create Initial Leads
            leadService.createLead(CreateLeadDto.builder()
                    .customerName("Acme Global Tech")
                    .companyName("Acme Corporation")
                    .mobile("+919811223344")
                    .email("contact@acmetech.com")
                    .city("Bangalore")
                    .state("Karnataka")
                    .country("India")
                    .requirement("Enterprise CRM license for 50 sales agents")
                    .leadSource(LeadSource.QR_CODE)
                    .leadCategory(LeadCategory.HOT)
                    .leadStatus(LeadStatus.NEW)
                    .priority(Priority.HIGH)
                    .leadValue(150000.0)
                    .assignedEmployeeId(vaibhav.getId())
                    .nextFollowup(LocalDateTime.now().plusDays(1))
                    .notes("Scanned digital visiting card at Mumbai Tech Expo")
                    .companyId(1L)
                    .build());

            leadService.createLead(CreateLeadDto.builder()
                    .customerName("Rajesh Sharma")
                    .companyName("Skyline Real Estate")
                    .mobile("+919822334455")
                    .email("rajesh@skylinerealty.com")
                    .city("Hyderabad")
                    .state("Telangana")
                    .country("India")
                    .requirement("Digital business cards for 20 real estate brokers")
                    .leadSource(LeadSource.DIGITAL_CARD)
                    .leadCategory(LeadCategory.WARM)
                    .leadStatus(LeadStatus.CONTACTED)
                    .priority(Priority.MEDIUM)
                    .leadValue(45000.0)
                    .assignedEmployeeId(vaibhav.getId())
                    .nextFollowup(LocalDateTime.now().plusHours(4))
                    .notes("Enquiry submitted via employee visiting card web link")
                    .companyId(1L)
                    .build());

            log.info("Demo leads initialized successfully!");
        }
    }
}
