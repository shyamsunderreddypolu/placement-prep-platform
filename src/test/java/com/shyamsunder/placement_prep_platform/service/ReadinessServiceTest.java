package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ReadinessResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.Streak;
import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
import com.shyamsunder.placement_prep_platform.repository.StreakRepository;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadinessServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private StreakRepository streakRepository;

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ReadinessService readinessService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("student@test.com").name("Student").build();
    }

    @Test
    void calculateReadiness_newUser_returnsLowScoreWithRecommendations() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(submissionRepository.countSolvedProblemsByDifficulty(1L, SubmissionStatus.SOLVED)).thenReturn(Collections.emptyList());
        when(submissionRepository.countSolvedProblemsByTopic(1L, SubmissionStatus.SOLVED)).thenReturn(Collections.emptyList());
        when(streakRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(resumeRepository.findByUserIdOrderByUploadedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(submissionRepository.findDueRevisions(eq(1L), eq(SubmissionStatus.SOLVED), any())).thenReturn(Collections.emptyList());

        ReadinessResponse response = readinessService.calculateReadiness("student@test.com");

        assertNotNull(response);
        assertEquals(0, response.getOverallScore());
        assertEquals(0, response.getCategoryScores().get("DSA"));
        assertEquals(0, response.getCategoryScores().get("Resume"));
        assertFalse(response.getWeaknesses().isEmpty());
        assertFalse(response.getRecommendations().isEmpty());
    }

    @Test
    void calculateReadiness_activeUser_returnsHighReadinessScore() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));

        List<Object[]> diffCounts = List.of(
                new Object[]{Difficulty.EASY, 15L},
                new Object[]{Difficulty.MEDIUM, 20L},
                new Object[]{Difficulty.HARD, 5L}
        );
        when(submissionRepository.countSolvedProblemsByDifficulty(1L, SubmissionStatus.SOLVED)).thenReturn(diffCounts);

        List<Object[]> topicCounts = List.of(
                new Object[]{"Arrays", 10L},
                new Object[]{"Two Pointers", 8L},
                new Object[]{"Linked List", 6L},
                new Object[]{"Tree", 8L},
                new Object[]{"Graph", 4L},
                new Object[]{"Dynamic Programming", 4L}
        );
        when(submissionRepository.countSolvedProblemsByTopic(1L, SubmissionStatus.SOLVED)).thenReturn(topicCounts);

        Streak streak = Streak.builder()
                .currentStreak(5)
                .longestStreak(10)
                .lastActiveDate(LocalDate.now())
                .build();
        when(streakRepository.findByUserId(1L)).thenReturn(Optional.of(streak));

        Resume resume = Resume.builder().id(1L).fileName("resume.pdf").build();
        when(resumeRepository.findByUserIdOrderByUploadedAtDesc(1L)).thenReturn(List.of(resume));
        when(submissionRepository.findDueRevisions(eq(1L), eq(SubmissionStatus.SOLVED), any())).thenReturn(Collections.emptyList());

        ReadinessResponse response = readinessService.calculateReadiness("student@test.com");

        assertNotNull(response);
        assertTrue(response.getOverallScore() >= 60);
        assertTrue(response.getCategoryScores().get("DSA") > 50);
        assertEquals(75, response.getCategoryScores().get("Resume"));
        assertFalse(response.getStrengths().isEmpty());
    }

    @Test
    void calculateReadiness_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> readinessService.calculateReadiness("unknown@test.com"));
    }
}
