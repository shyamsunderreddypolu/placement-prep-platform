package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.StreakResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Streak;
import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final StreakService streakService;

    public Map<Difficulty, Long> getSolvedProblemsCountByDifficulty(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        Map<Difficulty, Long> counts = new EnumMap<>(Difficulty.class);
        for (Difficulty difficulty : Difficulty.values()) {
            counts.put(difficulty, 0L);
        }

        List<Object[]> results = submissionRepository.countSolvedProblemsByDifficulty(user.getId(), SubmissionStatus.SOLVED);
        for (Object[] result : results) {
            Difficulty difficulty = (Difficulty) result[0];
            Long count = (Long) result[1];
            counts.put(difficulty, count);
        }

        return counts;
    }

    public Map<String, Long> getSolvedProblemsCountByTopic(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        List<Object[]> results = submissionRepository.countSolvedProblemsByTopic(user.getId(), SubmissionStatus.SOLVED);
        Map<String, Long> counts = new java.util.HashMap<>();
        for (Object[] result : results) {
            String topic = (String) result[0];
            Long count = (Long) result[1];
            counts.put(topic, count);
        }

        return counts;
    }

    public StreakResponse getUserStreak(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        Streak streak = streakService.getOrCreateStreak(user);
        return StreakResponse.builder()
                .currentStreak(streak.getCurrentStreak())
                .longestStreak(streak.getLongestStreak())
                .lastActiveDate(streak.getLastActiveDate())
                .build();
    }
}
