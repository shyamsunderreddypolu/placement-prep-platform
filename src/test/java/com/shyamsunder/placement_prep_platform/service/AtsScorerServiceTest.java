package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisRequest;
import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.User;
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

import java.time.LocalDateTime;
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

    private User mockUser;
    private Resume mockResume;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Student User")
                .email("student@test.com")
                .passwordHash("password")
                .build();

        mockResume = Resume.builder()
                .id(100L)
                .user(mockUser)
                .fileName("java_developer_resume.pdf")
                .fileUrl("/uploads/java_developer_resume.pdf")
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void analyzeResume_success_matchedAndMissingSkills() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        when(resumeRepository.findById(100L)).thenReturn(Optional.of(mockResume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(100L)
                .jobDescription("Java, Spring Boot, MySQL, React, Docker, Python")
                .build();

        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);

        assertNotNull(response);
        assertEquals(100L, response.getResumeId());
        assertEquals("java_developer_resume.pdf", response.getFileName());
        assertTrue(response.getMatchedSkills().contains("java"));
        assertFalse(response.getRecommendations().isEmpty());
    }

    @Test
    void analyzeResume_unauthorizedUser_throwsException() {
        User otherUser = User.builder().id(2L).email("other@test.com").build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));
        
        Resume otherUserResume = Resume.builder()
                .id(200L)
                .user(otherUser)
                .fileName("other_resume.pdf")
                .fileUrl("/uploads/other.pdf")
                .build();

        when(resumeRepository.findById(200L)).thenReturn(Optional.of(otherUserResume));

        AtsAnalysisRequest request = AtsAnalysisRequest.builder()
                .resumeId(200L)
                .jobDescription("Java, MySQL")
                .build();

        assertThrows(IllegalArgumentException.class, () -> atsScorerService.analyzeResume(request));
    }
}
