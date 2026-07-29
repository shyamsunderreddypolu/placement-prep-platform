package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ResumeResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        String fileUrl = storageService.uploadFile(file);

        Resume resume = Resume.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
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
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        return resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId()).stream()
                .map(resume -> ResumeResponse.builder()
                        .id(resume.getId())
                        .fileName(resume.getFileName())
                        .fileUrl(resume.getFileUrl())
                        .uploadedAt(resume.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
