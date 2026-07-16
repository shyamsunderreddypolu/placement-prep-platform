package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.SubmissionRequest;
import com.shyamsunder.placement_prep_platform.dto.SubmissionResponse;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import com.shyamsunder.placement_prep_platform.entity.Submission;
import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final StreakService streakService;

    @Transactional
    public SubmissionResponse logSubmission(SubmissionRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + request.getProblemId()));

        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .status(request.getStatus())
                .notes(request.getNotes())
                .build();

        Submission savedSubmission = submissionRepository.save(submission);

        // Update solved streak if submission is successful
        if (request.getStatus() == SubmissionStatus.SOLVED) {
            streakService.updateStreak(user);
        }

        return mapToResponse(savedSubmission);
    }

    public List<SubmissionResponse> getSubmissionHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .status(submission.getStatus())
                .notes(submission.getNotes())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
