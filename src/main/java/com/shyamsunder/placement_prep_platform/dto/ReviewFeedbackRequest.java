package com.shyamsunder.placement_prep_platform.dto;

import com.shyamsunder.placement_prep_platform.entity.ReviewFeedback;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFeedbackRequest {
    @NotNull(message = "Review feedback is required")
    private ReviewFeedback feedback;
}
