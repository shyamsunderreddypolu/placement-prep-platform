package com.shyamsunder.placement_prep_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsAnalysisRequest {

    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    private String jobDescription;
}
