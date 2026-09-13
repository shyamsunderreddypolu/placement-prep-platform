package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ReadinessResponse;
import com.shyamsunder.placement_prep_platform.service.ReadinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/readiness")
@RequiredArgsConstructor
@Tag(name = "Placement Readiness", description = "Endpoints for multi-dimensional placement readiness score calculations")
public class ReadinessController {

    private final ReadinessService readinessService;

    @Operation(summary = "Calculate Placement Readiness Index (PRI)", description = "Computes deterministic readiness score based on DSA Solve Ratio (35%), Resume Quality (25%), Consistency & Streaks (20%), and Core Topic Breadth (20%)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Readiness score calculated successfully")
    })
    @GetMapping
    public ResponseEntity<ReadinessResponse> getPlacementReadiness(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(readinessService.calculateReadiness(email));
    }
}