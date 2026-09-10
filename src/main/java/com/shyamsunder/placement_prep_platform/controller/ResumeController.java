package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ResumeResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.uploadResume(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getUserResumes() {
        List<ResumeResponse> responses = resumeService.getUserResumes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {
        Resume resume = resumeService.getResumeByIdWithOwnershipCheck(id);
        Resource resource = resumeService.loadResumeResource(id);

        String contentType = "application/octet-stream";
        if (resume.getFileName() != null) {
            String lower = resume.getFileName().toLowerCase();
            if (lower.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lower.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
                .body(resource);
    }
}
