package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.SubmissionRequest;
import com.shyamsunder.placement_prep_platform.dto.SubmissionResponse;
import com.shyamsunder.placement_prep_platform.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Submissions & History", description = "Endpoints for logging problem attempts and retrieving submission history")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Log problem attempt", description = "Records a user's practice attempt, updates streak, and initializes 1-4-7 revision schedule if solved")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Submission successfully logged"),
            @ApiResponse(responseCode = "400", description = "Invalid submission payload"),
            @ApiResponse(responseCode = "404", description = "Problem or user not found")
    })
    @PostMapping
    public ResponseEntity<SubmissionResponse> logSubmission(
            @Valid @RequestBody SubmissionRequest request,
            Principal principal
    ) {
        String email = principal.getName();
        SubmissionResponse response = submissionService.logSubmission(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user submission history", description = "Retrieves past problem attempts for the authenticated user with optional pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission history retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getSubmissionHistory(
            Principal principal,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page") @RequestParam(required = false) Integer size
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