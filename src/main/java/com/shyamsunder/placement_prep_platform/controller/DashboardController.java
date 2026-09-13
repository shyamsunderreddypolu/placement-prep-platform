package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.StreakResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.service.DashboardService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Analytics", description = "Endpoints for dashboard metrics, streaks, and progress visualization")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get solved problem counts by difficulty", description = "Aggregates problems solved by the user grouped into EASY, MEDIUM, and HARD")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Difficulty statistics retrieved successfully")
    })
    @GetMapping("/difficulty")
    public ResponseEntity<Map<Difficulty, Long>> getSolvedProblemsByDifficulty(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(dashboardService.getSolvedProblemsCountByDifficulty(email));
    }

    @Operation(summary = "Get solved problem counts by topic", description = "Aggregates problems solved by the user across DSA topics (Arrays, Trees, etc.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topic statistics retrieved successfully")
    })
    @GetMapping("/topic")
    public ResponseEntity<Map<String, Long>> getSolvedProblemsByTopic(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(dashboardService.getSolvedProblemsCountByTopic(email));
    }

    @Operation(summary = "Get user activity streak", description = "Returns current streak, longest streak, and last active date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Streak details retrieved successfully")
    })
    @GetMapping("/streak")
    public ResponseEntity<StreakResponse> getUserStreak(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(dashboardService.getUserStreak(email));
    }
}