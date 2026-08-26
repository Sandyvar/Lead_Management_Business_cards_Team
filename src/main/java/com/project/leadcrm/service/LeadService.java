package com.project.leadcrm.service;

import com.project.leadcrm.dto.AssignLeadDto;
import com.project.leadcrm.dto.CreateLeadDto;
import com.project.leadcrm.dto.LeadDto;
import com.project.leadcrm.dto.UpdateLeadStatusDto;
import com.project.leadcrm.model.enums.LeadStatus;

import java.util.List;

public interface LeadService {

    LeadDto createLead(CreateLeadDto createLeadDto);

    LeadDto getLeadById(Long id);

    List<LeadDto> getAllLeads();

    List<LeadDto> getLeadsByStatus(LeadStatus status);

    List<LeadDto> getLeadsByEmployee(Long employeeId);

    LeadDto updateLeadStatus(Long id, UpdateLeadStatusDto updateDto);

    LeadDto assignLead(Long id, AssignLeadDto assignDto);

    void deleteLead(Long id);

    com.project.leadcrm.dto.PipelineBoardDto getPipelineBoard();
}
