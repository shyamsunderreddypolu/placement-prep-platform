package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.entity.Streak;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.StreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final StreakRepository streakRepository;

    @Transactional
    public Streak updateStreak(User user) {
        Streak streak = streakRepository.findByUserId(user.getId())
                .orElseGet(() -> Streak.builder()
                        .user(user)
                        .currentStreak(0)
                        .longestStreak(0)
                        .lastActiveDate(null)
                        .build());

        LocalDate today = LocalDate.now();
        LocalDate lastActive = streak.getLastActiveDate();

        if (lastActive == null) {
            streak.setCurrentStreak(1);
            streak.setLastActiveDate(today);
        } else if (lastActive.equals(today)) {
            // Already submitted a solved problem today, keep streak as is
        } else if (lastActive.equals(today.minusDays(1))) {
            // Submitted yesterday, increment streak
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
            streak.setLastActiveDate(today);
        } else {
            // Gap is greater than 1 day, streak is broken, reset to 1
            streak.setCurrentStreak(1);
            streak.setLastActiveDate(today);
        }

        // Keep track of record streak
        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }

        return streakRepository.save(streak);
    }

    @Transactional(readOnly = true)
    public Streak getOrCreateStreak(User user) {
        return streakRepository.findByUserId(user.getId())
                .orElseGet(() -> Streak.builder()
                        .user(user)
                        .currentStreak(0)
                        .longestStreak(0)
                        .lastActiveDate(null)
                        .build());
    }
}
