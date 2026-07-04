package com.shyamsunder.placement_prep_platform.dto;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemResponse {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private String topic;
    private String link;
    private LocalDateTime createdAt;
}
