package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ProblemRequest;
import com.shyamsunder.placement_prep_platform.dto.ProblemResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<?> addProblem(
            @Valid @RequestBody ProblemRequest request,
            Principal principal
    ) {
        String email = principal.getName();

        // Simple domain and default check for administrator privileges
        if (!email.endsWith("@placementprep.com") && !email.equalsIgnoreCase("admin@gmail.com")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only administrators can add problems.");
        }

        ProblemResponse response = problemService.addProblem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getProblems(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Difficulty difficulty
    ) {
        return ResponseEntity.ok(problemService.getProblems(topic, difficulty));
    }
}
