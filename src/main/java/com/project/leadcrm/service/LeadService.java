package com.project.leadcrm.service;

import com.project.leadcrm.dto.LeadRequest;
import com.project.leadcrm.dto.LeadResponse;
import com.project.leadcrm.exception.ResourceNotFoundException;
import com.project.leadcrm.model.Lead;
import com.project.leadcrm.model.LeadStatus;
import com.project.leadcrm.repository.LeadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Transactional(readOnly = true)
    public Page<LeadResponse> findAll(LeadStatus status, Pageable pageable) {
        Page<Lead> leads = status == null
                ? leadRepository.findAll(pageable)
                : leadRepository.findByStatus(status, pageable);
        return leads.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public LeadResponse findById(Long id) {
        return toResponse(getLead(id));
    }

    @Transactional
    public LeadResponse create(LeadRequest request) {
        Lead lead = new Lead();
        applyRequest(lead, request);
        return toResponse(leadRepository.save(lead));
    }

    @Transactional
    public LeadResponse update(Long id, LeadRequest request) {
        Lead lead = getLead(id);
        applyRequest(lead, request);
        return toResponse(lead);
    }

    @Transactional
    public void delete(Long id) {
        Lead lead = getLead(id);
        leadRepository.delete(lead);
    }

    private Lead getLead(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead %d was not found".formatted(id)));
    }

    private void applyRequest(Lead lead, LeadRequest request) {
        lead.setName(request.name());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setCompany(request.company());
        lead.setStatus(request.status() == null ? LeadStatus.NEW : request.status());
        lead.setNotes(request.notes());
    }

    private LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getName(),
                lead.getEmail(),
                lead.getPhone(),
                lead.getCompany(),
                lead.getStatus(),
                lead.getNotes(),
                lead.getCreatedAt(),
                lead.getUpdatedAt()
        );
    }
}
