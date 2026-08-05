package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.SubmissionRequest;
import com.shyamsunder.placement_prep_platform.dto.SubmissionResponse;
import com.shyamsunder.placement_prep_platform.entity.*;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private StreakService streakService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SubmissionService submissionService;

    private User mockUser;
    private Problem mockProblem;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Student User")
                .email("student@test.com")
                .build();

        mockProblem = Problem.builder()
                .id(10L)
                .title("Two Sum")
                .topic("Arrays")
                .difficulty(Difficulty.EASY)
                .link("https://leetcode.com/problems/two-sum")
                .build();
    }

    @Test
    void logSubmission_solvedStatus_updatesStreakAndSavesSubmission() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        when(problemRepository.findById(10L)).thenReturn(Optional.of(mockProblem));

        Submission savedSubmission = Submission.builder()
                .id(100L)
                .user(mockUser)
                .problem(mockProblem)
                .status(SubmissionStatus.SOLVED)
                .notes("Used HashMap O(N) approach")
                .submittedAt(LocalDateTime.now())
                .build();

        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);

        SubmissionRequest request = SubmissionRequest.builder()
                .problemId(10L)
                .status(SubmissionStatus.SOLVED)
                .notes("Used HashMap O(N) approach")
                .build();

        SubmissionResponse response = submissionService.logSubmission(request, "student@test.com");

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Two Sum", response.getProblemTitle());
        assertEquals(SubmissionStatus.SOLVED, response.getStatus());

        verify(streakService, times(1)).updateStreak(mockUser);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void logSubmission_attemptedStatus_doesNotTriggerStreakUpdate() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        when(problemRepository.findById(10L)).thenReturn(Optional.of(mockProblem));

        Submission savedSubmission = Submission.builder()
                .id(101L)
                .user(mockUser)
                .problem(mockProblem)
                .status(SubmissionStatus.ATTEMPTED)
                .notes("Encountered TLE on large arrays")
                .submittedAt(LocalDateTime.now())
                .build();

        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);

        SubmissionRequest request = SubmissionRequest.builder()
                .problemId(10L)
                .status(SubmissionStatus.ATTEMPTED)
                .notes("Encountered TLE on large arrays")
                .build();

        SubmissionResponse response = submissionService.logSubmission(request, "student@test.com");

        assertNotNull(response);
        assertEquals(SubmissionStatus.ATTEMPTED, response.getStatus());

        verify(streakService, never()).updateStreak(any(User.class));
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void getUserSubmissions_returnsSubmissionHistory() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));

        Submission sub1 = Submission.builder()
                .id(100L)
                .user(mockUser)
                .problem(mockProblem)
                .status(SubmissionStatus.SOLVED)
                .submittedAt(LocalDateTime.now())
                .build();

        when(submissionRepository.findByUserIdOrderBySubmittedAtDesc(1L)).thenReturn(List.of(sub1));

        List<SubmissionResponse> history = submissionService.getSubmissionHistory("student@test.com");

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("Two Sum", history.get(0).getProblemTitle());
        verify(submissionRepository, times(1)).findByUserIdOrderBySubmittedAtDesc(1L);
    }
}
