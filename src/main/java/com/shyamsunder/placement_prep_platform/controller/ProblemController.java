package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ProblemRequest;
import com.shyamsunder.placement_prep_platform.dto.ProblemResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.service.ProblemService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Tag(name = "DSA Problems", description = "Endpoints for querying problem repositories, patterns, and administrative problem creation")
public class ProblemController {

    private final ProblemService problemService;

    @Operation(summary = "Create a new problem (Admin only)", description = "Adds a new problem with topic, difficulty, pattern, and practice link")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Problem successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "Access denied: Requires ROLE_ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> addProblem(@Valid @RequestBody ProblemRequest request) {
        ProblemResponse response = problemService.addProblem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get problems with optional filtering and pagination", description = "Retrieves problems filtered by topic, difficulty, or pattern. Supports page and size query parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getProblems(
            @Parameter(description = "Filter by topic (e.g. Arrays, Trees)") @RequestParam(required = false) String topic,
            @Parameter(description = "Filter by difficulty (EASY, MEDIUM, HARD)") @RequestParam(required = false) Difficulty difficulty,
            @Parameter(description = "Filter by pattern (e.g. Two Pointers, Sliding Window)") @RequestParam(required = false) String pattern,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page") @RequestParam(required = false) Integer size
    ) {
        if (page != null) {
            int pageSize = (size != null && size > 0) ? size : 20;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
            Page<ProblemResponse> pagedResult = problemService.getPagedProblems(topic, difficulty, pattern, pageable);
            return ResponseEntity.ok(pagedResult);
        }
        return ResponseEntity.ok(problemService.getProblems(topic, difficulty, pattern));
    }

    @Operation(summary = "Get distinct algorithm patterns", description = "Returns a unique list of all algorithmic patterns in the repository")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of patterns retrieved successfully")
    })
    @GetMapping("/patterns")
    public ResponseEntity<List<String>> getPatterns() {
        return ResponseEntity.ok(problemService.getDistinctPatterns());
    }
}