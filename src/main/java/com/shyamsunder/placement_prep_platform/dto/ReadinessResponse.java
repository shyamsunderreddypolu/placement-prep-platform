package com.shyamsunder.placement_prep_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadinessResponse {
    private int overallScore;
    private Map<String, Integer> categoryScores;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    private Map<String, Object> metrics;
}
