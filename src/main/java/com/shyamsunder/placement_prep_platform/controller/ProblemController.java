package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ProblemRequest;
import com.shyamsunder.placement_prep_platform.dto.ProblemResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> addProblem(@Valid @RequestBody ProblemRequest request) {
        ProblemResponse response = problemService.addProblem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getProblems(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String pattern
    ) {
        return ResponseEntity.ok(problemService.getProblems(topic, difficulty, pattern));
    }

    @GetMapping("/patterns")
    public ResponseEntity<List<String>> getPatterns() {
        return ResponseEntity.ok(problemService.getDistinctPatterns());
    }
}
