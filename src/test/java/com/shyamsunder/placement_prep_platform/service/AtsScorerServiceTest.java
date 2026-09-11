package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisRequest;
import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.ForbiddenException;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtsScorerServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AtsScorerService atsScorerService;

    private User user;
    private Resume resume;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("student@test.com")
                .name("Student")
                .build();

        resume = Resume.builder()
                .id(10L)
                .user(user)
                .fileName("Student_Resume.pdf")
                .fileUrl("uuid-123.pdf")
                .build();
    }

    @Test
    void analyzeResume_unauthorizedUser_throwsForbiddenException() {
        User differentUser = User.builder().id(2L).email("hacker@test.com").build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("hacker@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("hacker@test.com")).thenReturn(Optional.of(differentUser));
        when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(10L)
                .jobDescription("Java Spring Boot Developer")
                .build();

        assertThrows(ForbiddenException.class, () -> atsScorerService.analyzeResume(request));
    }

    @Test
    void analyzeResume_validOwnership_returnsAnalysisResponse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(10L)
                .jobDescription("Java, React, MySQL")
                .build();

        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);

        assertNotNull(response);
        assertEquals(10L, response.getResumeId());
        assertTrue(response.getScore() >= 0);
    }

    @Test
    void analyzeResume_zeroMatches_returnsZeroScoreWithoutArtificialMinimum() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(10L)
                .jobDescription("Kubernetes, Rust, Flutter, Swift")
                .build();

        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);

        assertNotNull(response);
        // Score must NOT be inflated to 35% or have manufactured skills
        assertEquals(0, response.getScore());
        assertTrue(response.getMatchedSkills().isEmpty());
        assertFalse(response.getMissingSkills().isEmpty());
    }
}
