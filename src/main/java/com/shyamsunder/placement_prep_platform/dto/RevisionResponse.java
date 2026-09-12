package com.shyamsunder.placement_prep_platform.dto;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.ReviewFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevisionResponse {
    private Long submissionId;
    private Long problemId;
    private String problemTitle;
    private Difficulty difficulty;
    private String topic;
    private String pattern;
    private String link;
    private String notes;
    private LocalDateTime solvedAt;
    private LocalDateTime nextReviewAt;
    private int reviewCount;
    private ReviewFeedback lastReviewFeedback;
}
