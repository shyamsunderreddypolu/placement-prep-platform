package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ResumeResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "Endpoints for resume uploads, listings, and secure downloads with ownership validation")
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "Upload a resume document", description = "Uploads a PDF or DOCX resume with magic-byte validation and 5MB maximum file size")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format or file size exceeded")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.uploadResume(file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's uploaded resumes", description = "Retrieves all resumes uploaded by the authenticated user with optional pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumes retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getUserResumes(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page") @RequestParam(required = false) Integer size
    ) {
        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 20;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "uploadedAt"));
            Page<ResumeResponse> pagedResult = resumeService.getPagedUserResumes(pageable);
            return ResponseEntity.ok(pagedResult);
        }
        List<ResumeResponse> responses = resumeService.getUserResumes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Download resume file", description = "Streams the resume file content. Access is strictly enforced to verify user ownership.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File download stream"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not own this resume"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(
            @Parameter(description = "Resume ID") @PathVariable Long id
    ) {
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