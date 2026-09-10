package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ResumeResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.FileValidationException;
import com.shyamsunder.placement_prep_platform.exception.ForbiddenException;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final StorageService storageService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeResponse uploadResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File cannot be empty");
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        String storedFileName = storageService.uploadFile(file);

        Resume resume = Resume.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .fileUrl(storedFileName)
                .build();

        Resume savedResume = resumeRepository.save(resume);

        return ResumeResponse.builder()
                .id(savedResume.getId())
                .fileName(savedResume.getFileName())
                .fileUrl(savedResume.getFileUrl())
                .uploadedAt(savedResume.getUploadedAt())
                .build();
    }

    public List<ResumeResponse> getUserResumes() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        return resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId()).stream()
                .map(resume -> ResumeResponse.builder()
                        .id(resume.getId())
                        .fileName(resume.getFileName())
                        .fileUrl(resume.getFileUrl())
                        .uploadedAt(resume.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public Resume getResumeByIdWithOwnershipCheck(Long resumeId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied: You do not own this resume");
        }

        return resume;
    }

    public Resource loadResumeResource(Long resumeId) {
        Resume resume = getResumeByIdWithOwnershipCheck(resumeId);
        return storageService.loadAsResource(resume.getFileUrl());
    }
}
