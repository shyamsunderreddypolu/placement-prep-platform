package com.shyamsunder.placement_prep_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", indexes = {
    @Index(name = "idx_submission_user", columnList = "user_id"),
    @Index(name = "idx_submission_submitted_at", columnList = "submitted_at"),
    @Index(name = "idx_submission_next_review", columnList = "next_review_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "submitted_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime submittedAt;

    @Column(name = "solved_at", columnDefinition = "DATETIME")
    private LocalDateTime solvedAt;

    @Column(name = "next_review_at", columnDefinition = "DATETIME")
    private LocalDateTime nextReviewAt;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private int reviewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_review_feedback")
    private ReviewFeedback lastReviewFeedback;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (status == SubmissionStatus.SOLVED && solvedAt == null) {
            solvedAt = submittedAt;
            // 1-4-7 Rule: First revision is scheduled in 1 day
            nextReviewAt = submittedAt.plusDays(1);
        }
    }
}
