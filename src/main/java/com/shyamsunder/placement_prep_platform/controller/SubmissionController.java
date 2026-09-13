package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.SubmissionRequest;
import com.shyamsunder.placement_prep_platform.dto.SubmissionResponse;
import com.shyamsunder.placement_prep_platform.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> logSubmission(
            @Valid @RequestBody SubmissionRequest request,
            Principal principal
    ) {
        String email = principal.getName();
        SubmissionResponse response = submissionService.logSubmission(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getSubmissionHistory(
            Principal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        String email = principal.getName();
        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 20;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "submittedAt"));
            Page<SubmissionResponse> pagedResult = submissionService.getPagedSubmissionHistory(email, pageable);
            return ResponseEntity.ok(pagedResult);
        }
        List<SubmissionResponse> history = submissionService.getSubmissionHistory(email);
        return ResponseEntity.ok(history);
    }
}