package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ReviewFeedbackRequest;
import com.shyamsunder.placement_prep_platform.dto.RevisionResponse;
import com.shyamsunder.placement_prep_platform.service.RevisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/revisions")
@RequiredArgsConstructor
@Tag(name = "1-4-7 Spaced Repetition", description = "Endpoints for spaced repetition revision queues and interval feedback")
public class RevisionController {

    private final RevisionService revisionService;

    @Operation(summary = "Get problems due for revision today", description = "Returns problems solved by the user whose scheduled revision timestamp (Day 1, Day 4, Day 7, Day 14, Day 30) is due")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Due revisions retrieved successfully")
    })
    @GetMapping("/due")
    public ResponseEntity<List<RevisionResponse>> getDueRevisions(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(revisionService.getDueRevisions(email));
    }

    @Operation(summary = "Submit revision feedback", description = "Submits recall difficulty feedback (EASY, NEEDS_REVIEW, HARD) and recalculates next revision target")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review logged and next interval calculated"),
            @ApiResponse(responseCode = "403", description = "Access denied: You do not own this submission"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @PostMapping("/{submissionId}/review")
    public ResponseEntity<RevisionResponse> submitReview(
            @Parameter(description = "ID of the submission being reviewed") @PathVariable Long submissionId,
            @Valid @RequestBody ReviewFeedbackRequest request,
            Principal principal
    ) {
        String email = principal.getName();
        RevisionResponse response = revisionService.submitReview(submissionId, request.getFeedback(), email);
        return ResponseEntity.ok(response);
    }
}