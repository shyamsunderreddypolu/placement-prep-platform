package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.RevisionResponse;
import com.shyamsunder.placement_prep_platform.entity.*;
import com.shyamsunder.placement_prep_platform.exception.ForbiddenException;
import com.shyamsunder.placement_prep_platform.repository.SubmissionRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevisionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RevisionService revisionService;

    private User user;
    private Problem problem;
    private Submission submission;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("student@test.com").build();
        problem = Problem.builder().id(101L).title("LRU Cache").difficulty(Difficulty.MEDIUM).topic("Linked List").build();
        submission = Submission.builder()
                .id(50L)
                .user(user)
                .problem(problem)
                .status(SubmissionStatus.SOLVED)
                .solvedAt(LocalDateTime.now().minusDays(1))
                .nextReviewAt(LocalDateTime.now().minusHours(2)) // due
                .reviewCount(0)
                .build();
    }

    @Test
    void getDueRevisions_returnsDueSubmissions() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(submissionRepository.findDueRevisions(eq(1L), eq(SubmissionStatus.SOLVED), any(LocalDateTime.class)))
                .thenReturn(List.of(submission));

        List<RevisionResponse> due = revisionService.getDueRevisions("student@test.com");

        assertNotNull(due);
        assertEquals(1, due.size());
        assertEquals("LRU Cache", due.get(0).getProblemTitle());
    }

    @Test
    void submitReview_easyFeedback_schedulesDay4OnFirstReview() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(submissionRepository.findById(50L)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RevisionResponse response = revisionService.submitReview(50L, ReviewFeedback.EASY, "student@test.com");

        assertNotNull(response);
        assertEquals(1, response.getReviewCount());
        assertEquals(ReviewFeedback.EASY, response.getLastReviewFeedback());
        assertTrue(response.getNextReviewAt().isAfter(LocalDateTime.now().plusDays(2)));
    }

    @Test
    void submitReview_otherUser_throwsForbiddenException() {
        User otherUser = User.builder().id(99L).email("hacker@test.com").build();
        when(userRepository.findByEmail("hacker@test.com")).thenReturn(Optional.of(otherUser));
        when(submissionRepository.findById(50L)).thenReturn(Optional.of(submission));

        assertThrows(ForbiddenException.class, () ->
                revisionService.submitReview(50L, ReviewFeedback.HARD, "hacker@test.com")
        );
        verify(submissionRepository, never()).save(any());
    }
}
