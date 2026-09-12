package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ReviewFeedbackRequest;
import com.shyamsunder.placement_prep_platform.dto.RevisionResponse;
import com.shyamsunder.placement_prep_platform.service.RevisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/revisions")
@RequiredArgsConstructor
public class RevisionController {

    private final RevisionService revisionService;

    @GetMapping("/due")
    public ResponseEntity<List<RevisionResponse>> getDueRevisions(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(revisionService.getDueRevisions(email));
    }

    @PostMapping("/{submissionId}/review")
    public ResponseEntity<RevisionResponse> submitReview(
            @PathVariable Long submissionId,
            @Valid @RequestBody ReviewFeedbackRequest request,
            Principal principal
    ) {
        String email = principal.getName();
        RevisionResponse response = revisionService.submitReview(submissionId, request.getFeedback(), email);
        return ResponseEntity.ok(response);
    }
}
