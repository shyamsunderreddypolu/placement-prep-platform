package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.SubmissionRequest;
import com.shyamsunder.placement_prep_platform.dto.SubmissionResponse;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import com.shyamsunder.placement_prep_platform.entity.Submission;
import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private SubmissionService submissionService;

    private User mockUser;
    private Problem mockProblem;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).email("student@test.com").build();
        mockProblem = Problem.builder().id(100L).title("Two Sum").build();
    }

    @Test
    void logSubmission_success_updatesStreakWhenSolved() {
        SubmissionRequest request = SubmissionRequest.builder()
                .problemId(100L)
                .status(SubmissionStatus.SOLVED)
                .notes("Used hashmap approach")
                .build();

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        when(problemRepository.findById(100L)).thenReturn(Optional.of(mockProblem));

        Submission savedSubmission = Submission.builder()
                .id(10L)
                .user(mockUser)
                .problem(mockProblem)
                .status(SubmissionStatus.SOLVED)
                .notes("Used hashmap approach")
                .build();

        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);

        SubmissionResponse response = submissionService.logSubmission(request, "student@test.com");

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Two Sum", response.getProblemTitle());
        assertEquals(SubmissionStatus.SOLVED, response.getStatus());

        verify(streakService, times(1)).updateStreak(mockUser);
    }

    @Test
    void logSubmission_problemNotFound_throwsResourceNotFoundException() {
        SubmissionRequest request = SubmissionRequest.builder()
                .problemId(999L)
                .status(SubmissionStatus.SOLVED)
                .build();

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        when(problemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> submissionService.logSubmission(request, "student@test.com"));
        verifyNoInteractions(streakService);
    }

    @Test
    void logSubmission_attempted_doesNotUpdateStreak() {
        SubmissionRequest request = SubmissionRequest.builder()
                .problemId(100L)
                .status(SubmissionStatus.ATTEMPTED)
                .build();

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        when(problemRepository.findById(100L)).thenReturn(Optional.of(mockProblem));

        Submission savedSubmission = Submission.builder()
                .id(11L)
                .user(mockUser)
                .problem(mockProblem)
                .status(SubmissionStatus.ATTEMPTED)
                .build();

        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);

        SubmissionResponse response = submissionService.logSubmission(request, "student@test.com");

        assertNotNull(response);
        assertEquals(SubmissionStatus.ATTEMPTED, response.getStatus());
        verify(streakService, never()).updateStreak(any());
    }
}
