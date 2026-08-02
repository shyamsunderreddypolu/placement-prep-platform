package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisRequest;
import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisResponse;
import com.shyamsunder.placement_prep_platform.service.AtsScorerService;
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
public class AtsController {

    private final AtsScorerService atsScorerService;

    @PostMapping("/analyze")
    public ResponseEntity<AtsAnalysisResponse> analyzeResume(@Valid @RequestBody AtsAnalysisRequest request) {
        AtsAnalysisResponse response = atsScorerService.analyzeResume(request);
        return ResponseEntity.ok(response);
    }
}
