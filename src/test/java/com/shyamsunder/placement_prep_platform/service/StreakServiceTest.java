package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.entity.Streak;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.StreakRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreakServiceTest {

    @Mock
    private StreakRepository streakRepository;

    @InjectMocks
    private StreakService streakService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Placement Candidate")
                .email("candidate@test.com")
                .build();
    }

    @Test
    void updateStreak_firstTimeSubmission_initializesStreakToOne() {
        when(streakRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(streakRepository.save(any(Streak.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Streak result = streakService.updateStreak(mockUser);

        assertNotNull(result);
        assertEquals(1, result.getCurrentStreak());
        assertEquals(1, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());
        verify(streakRepository, times(1)).save(any(Streak.class));
    }

    @Test
    void updateStreak_sameDaySubmission_keepsStreakUnchanged() {
        Streak existingStreak = Streak.builder()
                .id(10L)
                .user(mockUser)
                .currentStreak(5)
                .longestStreak(5)
                .lastActiveDate(LocalDate.now())
                .build();

        when(streakRepository.findByUserId(1L)).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Streak result = streakService.updateStreak(mockUser);

        assertNotNull(result);
        assertEquals(5, result.getCurrentStreak());
        assertEquals(5, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());
    }

    @Test
    void updateStreak_consecutiveDaySubmission_incrementsStreak() {
        Streak existingStreak = Streak.builder()
                .id(10L)
                .user(mockUser)
                .currentStreak(4)
                .longestStreak(4)
                .lastActiveDate(LocalDate.now().minusDays(1))
                .build();

        when(streakRepository.findByUserId(1L)).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Streak result = streakService.updateStreak(mockUser);

        assertNotNull(result);
        assertEquals(5, result.getCurrentStreak());
        assertEquals(5, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());
    }

    @Test
    void updateStreak_gapGreaterThanOneDay_resetsCurrentStreakPreservesLongest() {
        Streak existingStreak = Streak.builder()
                .id(10L)
                .user(mockUser)
                .currentStreak(8)
                .longestStreak(12)
                .lastActiveDate(LocalDate.now().minusDays(3))
                .build();

        when(streakRepository.findByUserId(1L)).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Streak result = streakService.updateStreak(mockUser);

        assertNotNull(result);
        assertEquals(1, result.getCurrentStreak());
        assertEquals(12, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());
    }
}
