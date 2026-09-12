package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ReadinessResponse;
import com.shyamsunder.placement_prep_platform.entity.*;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
import com.shyamsunder.placement_prep_platform.repository.StreakRepository;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReadinessService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final ResumeRepository resumeRepository;
    private final StreakRepository streakRepository;
    private final ProblemRepository problemRepository;

    private static final List<String> BENCHMARK_TOPICS = List.of(
            "Arrays", "Two Pointers", "Linked List", "Tree", "Graph",
            "Dynamic Programming", "Stack", "Binary Search", "Heap", "Backtracking"
    );

    @Transactional(readOnly = true)
    public ReadinessResponse calculateReadiness(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Long userId = user.getId();

        // 1. Fetch Solved Problems & Difficulty distribution
        List<Object[]> difficultyCounts = submissionRepository.countSolvedProblemsByDifficulty(userId, SubmissionStatus.SOLVED);
        long easyCount = 0;
        long mediumCount = 0;
        long hardCount = 0;

        for (Object[] row : difficultyCounts) {
            Difficulty diff = (Difficulty) row[0];
            Long count = (Long) row[1];
            if (diff == Difficulty.EASY) easyCount = count;
            else if (diff == Difficulty.MEDIUM) mediumCount = count;
            else if (diff == Difficulty.HARD) hardCount = count;
        }
        long totalSolved = easyCount + mediumCount + hardCount;

        // DSA Score (35% weight): benchmark is 30 problems with weighted difficulty
        // Easy = 1 pt, Medium = 2.5 pts, Hard = 4 pts. Target = 60 pts
        double dsaPoints = (easyCount * 1.0) + (mediumCount * 2.5) + (hardCount * 4.0);
        int dsaScore = (int) Math.min(100, Math.round((dsaPoints / 60.0) * 100.0));

        // 2. Topic Coverage (20% weight)
        List<Object[]> topicCounts = submissionRepository.countSolvedProblemsByTopic(userId, SubmissionStatus.SOLVED);
        Map<String, Long> solvedByTopic = new HashMap<>();
        for (Object[] row : topicCounts) {
            solvedByTopic.put((String) row[0], (Long) row[1]);
        }

        long topicsCovered = BENCHMARK_TOPICS.stream()
                .filter(topic -> solvedByTopic.getOrDefault(topic, 0L) > 0)
                .count();
        int topicCoverageScore = (int) Math.round(((double) topicsCovered / BENCHMARK_TOPICS.size()) * 100.0);

        // 3. Consistency Score (20% weight)
        Streak streak = streakRepository.findByUserId(userId).orElse(null);
        int currentStreak = (streak != null) ? streak.getCurrentStreak() : 0;
        int longestStreak = (streak != null) ? streak.getLongestStreak() : 0;
        LocalDate lastActive = (streak != null) ? streak.getLastActiveDate() : null;
        boolean activeRecently = lastActive != null && lastActive.isAfter(LocalDate.now().minusDays(3));

        int consistencyScore = Math.min(100, (currentStreak * 12) + (longestStreak * 4) + (activeRecently ? 20 : 0));

        // 4. Resume Score (25% weight)
        List<Resume> userResumes = resumeRepository.findByUserIdOrderByUploadedAtDesc(userId);
        int resumeScore = 0;
        if (!userResumes.isEmpty()) {
            resumeScore = 75;
        }

        // 5. Revision health metric
        List<Submission> dueRevisions = submissionRepository.findDueRevisions(userId, SubmissionStatus.SOLVED, LocalDateTime.now());
        int overdueRevisionsCount = dueRevisions.size();

        // Calculate Overall Score (DSA 35%, Resume 25%, Consistency 20%, Topic Coverage 20%)
        int overallScore = (int) Math.round(
                (dsaScore * 0.35) +
                (resumeScore * 0.25) +
                (consistencyScore * 0.20) +
                (topicCoverageScore * 0.20)
        );
        overallScore = Math.min(100, Math.max(0, overallScore));

        // Determine Strengths & Weaknesses
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        if (dsaScore >= 70) {
            strengths.add("Strong DSA Problem Solving Foundation (" + totalSolved + " problems solved)");
        } else if (dsaScore < 40) {
            weaknesses.add("Low DSA Problem Count (" + totalSolved + " solved, recommend 30+)");
            recommendations.add("Solve at least 2 Medium difficulty problems this week.");
        }

        if (topicCoverageScore >= 70) {
            strengths.add("Broad Topic Coverage across Core Placement Patterns");
        } else {
            List<String> unattemptedTopics = new ArrayList<>();
            for (String benchmarkTopic : BENCHMARK_TOPICS) {
                if (solvedByTopic.getOrDefault(benchmarkTopic, 0L) == 0) {
                    unattemptedTopics.add(benchmarkTopic);
                }
            }
            if (!unattemptedTopics.isEmpty()) {
                String weakTopicSample = String.join(", ", unattemptedTopics.subList(0, Math.min(3, unattemptedTopics.size())));
                weaknesses.add("Uncovered Topics: " + weakTopicSample);
                recommendations.add("Start practicing fundamentals in: " + unattemptedTopics.get(0));
            }
        }

        if (currentStreak >= 3) {
            strengths.add("Active Solving Streak (" + currentStreak + " days consecutive)");
        } else {
            weaknesses.add("Low Daily Solving Consistency");
            recommendations.add("Solve at least 1 problem today to build your placement preparation streak.");
        }

        if (resumeScore > 0) {
            strengths.add("Placement Resume Uploaded and Ready for ATS Evaluation");
        } else {
            weaknesses.add("Missing Resume on Profile");
            recommendations.add("Upload your PDF resume to evaluate ATS compatibility against target job descriptions.");
        }

        if (overdueRevisionsCount > 0) {
            weaknesses.add(overdueRevisionsCount + " Problem Revision(s) Currently Due");
            recommendations.add("Complete your pending spaced repetition reviews in the Revision tab.");
        } else if (totalSolved > 0) {
            strengths.add("1-4-7 Spaced Repetition Reviews Up to Date");
        }

        Map<String, Integer> categoryScores = new LinkedHashMap<>();
        categoryScores.put("DSA", dsaScore);
        categoryScores.put("Resume", resumeScore);
        categoryScores.put("Consistency", consistencyScore);
        categoryScores.put("TopicCoverage", topicCoverageScore);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalSolved", totalSolved);
        metrics.put("easySolved", easyCount);
        metrics.put("mediumSolved", mediumCount);
        metrics.put("hardSolved", hardCount);
        metrics.put("currentStreak", currentStreak);
        metrics.put("longestStreak", longestStreak);
        metrics.put("topicsCoveredCount", topicsCovered);
        metrics.put("overdueRevisionsCount", overdueRevisionsCount);
        metrics.put("hasResume", !userResumes.isEmpty());

        return ReadinessResponse.builder()
                .overallScore(overallScore)
                .categoryScores(categoryScores)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .recommendations(recommendations)
                .metrics(metrics)
                .build();
    }
}
