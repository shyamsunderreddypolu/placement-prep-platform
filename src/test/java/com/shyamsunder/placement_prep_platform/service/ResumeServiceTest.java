package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ResumeResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ResumeService resumeService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .passwordHash("password")
                .build();
    }

    @Test
    void uploadResume_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample_resume.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@test.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(mockUser));
        when(storageService.uploadFile(file)).thenReturn("/uploads/sample_resume.pdf");

        Resume savedResume = Resume.builder()
                .id(10L)
                .user(mockUser)
                .fileName("sample_resume.pdf")
                .fileUrl("/uploads/sample_resume.pdf")
                .uploadedAt(LocalDateTime.now())
                .build();

        when(resumeRepository.save(any(Resume.class))).thenReturn(savedResume);

        ResumeResponse response = resumeService.uploadResume(file);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("sample_resume.pdf", response.getFileName());
        assertEquals("/uploads/sample_resume.pdf", response.getFileUrl());

        verify(storageService, times(1)).uploadFile(file);
        verify(resumeRepository, times(1)).save(any(Resume.class));
    }

    @Test
    void uploadResume_emptyFile_throwsException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> resumeService.uploadResume(emptyFile));
        verifyNoInteractions(storageService);
    }
}
