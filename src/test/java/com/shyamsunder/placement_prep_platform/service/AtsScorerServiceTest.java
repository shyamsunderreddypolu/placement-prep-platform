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
                .fileName("Student_Resume_Java_SpringBoot_SQL.pdf")
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
    void analyzeResume_validOwnership_returnsAnalysisResponseWithBreakdown() {
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
        assertNotNull(response.getScoreBreakdown());
        assertTrue(response.getScoreBreakdown().containsKey("keywordMatch"));
        assertTrue(response.getScoreBreakdown().containsKey("technicalBreadth"));
        assertTrue(response.getScoreBreakdown().containsKey("experience"));
        assertTrue(response.getScoreBreakdown().containsKey("education"));
        assertTrue(response.getScoreBreakdown().containsKey("projects"));
    }

    @Test
    void analyzeResume_zeroMatches_returnsZeroScoreWithoutArtificialMinimum() {
        Resume emptyResume = Resume.builder()
                .id(20L)
                .user(user)
                .fileName("")
                .fileUrl("empty.pdf")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(resumeRepository.findById(20L)).thenReturn(Optional.of(emptyResume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(20L)
                .jobDescription("Rust, Flutter, Swift, Dart")
                .build();

        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);

        assertNotNull(response);
        assertEquals(0, response.getScore());
        assertTrue(response.getMatchedSkills().isEmpty());
        assertFalse(response.getMissingSkills().isEmpty());
    }

    @Test
    void analyzeResume_synonymNormalization_matchesVariants() {
        Resume synonymResume = Resume.builder()
                .id(30L)
                .user(user)
                .fileName("John_Doe_springboot_js_k8s.pdf")
                .fileUrl("synonym.pdf")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(resumeRepository.findById(30L)).thenReturn(Optional.of(synonymResume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(30L)
                .jobDescription("Spring Boot, JavaScript, Kubernetes")
                .build();

        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);

        assertNotNull(response);
        assertTrue(response.getMatchedSkills().contains("Spring Boot"));
        assertTrue(response.getMatchedSkills().contains("JavaScript"));
        assertTrue(response.getMatchedSkills().contains("Kubernetes"));
    }
}
