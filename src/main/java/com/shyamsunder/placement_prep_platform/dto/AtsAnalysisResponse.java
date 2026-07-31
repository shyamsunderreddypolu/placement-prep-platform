package com.shyamsunder.placement_prep_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsAnalysisResponse {
    private Long resumeId;
    private String fileName;
    private int score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> recommendations;
}
