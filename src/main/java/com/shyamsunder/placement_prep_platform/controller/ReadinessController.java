package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.ReadinessResponse;
import com.shyamsunder.placement_prep_platform.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/readiness")
@RequiredArgsConstructor
public class ReadinessController {

    private final ReadinessService readinessService;

    @GetMapping
    public ResponseEntity<ReadinessResponse> getPlacementReadiness(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(readinessService.calculateReadiness(email));
    }
}
