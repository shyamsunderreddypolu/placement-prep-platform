package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisRequest;
import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisResponse;
import com.shyamsunder.placement_prep_platform.service.AtsScorerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ats")
@RequiredArgsConstructor
@Tag(name = "ATS Resume Evaluator", description = "Endpoints for transparent, weighted resume scoring and keyword extraction")
public class AtsController {

    private final AtsScorerService atsScorerService;

    @Operation(summary = "Analyze resume text against job description", description = "Performs transparent, weighted 5-dimension ATS evaluation (Keyword 40%, Breadth 25%, Experience 15%, Education 10%, Projects 10%) with synonym expansion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume analyzed successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or empty resume/job description text")
    })
    @PostMapping("/analyze")
    public ResponseEntity<AtsAnalysisResponse> analyzeResume(@Valid @RequestBody AtsAnalysisRequest request) {
        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);
        return ResponseEntity.ok(response);
    }
}