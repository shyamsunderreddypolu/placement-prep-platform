package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.service.DashboardService;
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
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/difficulty")
    public ResponseEntity<Map<Difficulty, Long>> getSolvedProblemsByDifficulty(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(dashboardService.getSolvedProblemsCountByDifficulty(email));
    }
}
