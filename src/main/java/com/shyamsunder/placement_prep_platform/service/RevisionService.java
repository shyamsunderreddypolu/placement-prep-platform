package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.RevisionResponse;
import com.shyamsunder.placement_prep_platform.entity.ReviewFeedback;
import com.shyamsunder.placement_prep_platform.entity.Submission;
import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.ForbiddenException;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RevisionResponse> getDueRevisions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        LocalDateTime now = LocalDateTime.now();
        List<Submission> dueSubmissions = submissionRepository.findDueRevisions(user.getId(), SubmissionStatus.SOLVED, now);

        return dueSubmissions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RevisionResponse submitReview(Long submissionId, ReviewFeedback feedback, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        if (!submission.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied: You do not own this submission");
        }

        int currentCount = submission.getReviewCount();
        int daysToAdd;

        // 1-4-7 Spaced Repetition logic with feedback adaptation
        switch (feedback) {
            case EASY:
                if (currentCount == 0) {
                    daysToAdd = 3; // Day 1 + 3 -> Day 4
                } else if (currentCount == 1) {
                    daysToAdd = 3; // Day 4 + 3 -> Day 7
                } else {
                    daysToAdd = 7; // Mastered: 7-day retention interval
                }
                break;
            case NEEDS_REVIEW:
                daysToAdd = (currentCount >= 2) ? 3 : 2;
                break;
            case HARD:
            default:
                daysToAdd = 1; // Needs immediate repetition tomorrow
                break;
        }

        submission.setReviewCount(currentCount + 1);
        submission.setLastReviewFeedback(feedback);
        submission.setNextReviewAt(LocalDateTime.now().plusDays(daysToAdd));

        Submission updated = submissionRepository.save(submission);
        return mapToResponse(updated);
    }

    private RevisionResponse mapToResponse(Submission submission) {
        return RevisionResponse.builder()
                .submissionId(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .difficulty(submission.getProblem().getDifficulty())
                .topic(submission.getProblem().getTopic())
                .pattern(submission.getProblem().getPattern())
                .link(submission.getProblem().getLink())
                .notes(submission.getNotes())
                .solvedAt(submission.getSolvedAt())
                .nextReviewAt(submission.getNextReviewAt())
                .reviewCount(submission.getReviewCount())
                .lastReviewFeedback(submission.getLastReviewFeedback())
                .build();
    }
}
