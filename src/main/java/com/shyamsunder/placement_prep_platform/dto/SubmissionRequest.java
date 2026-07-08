package com.shyamsunder.placement_prep_platform.dto;

import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotNull(message = "Submission status is required")
    private SubmissionStatus status;

    private String notes;
}
