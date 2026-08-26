package com.project.leadcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineBoardDto {

    private int totalLeads;
    private double totalPipelineValue;
    private List<PipelineStageGroupDto> stages;
}
