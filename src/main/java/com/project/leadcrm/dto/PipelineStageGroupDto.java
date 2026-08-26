package com.project.leadcrm.dto;

import com.project.leadcrm.model.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineStageGroupDto {

    private LeadStatus stage;
    private String stageDisplayName;
    private int leadCount;
    private double totalValue;
    private List<LeadDto> leads;
}
