package com.shyamsunder.placement_prep_platform.controller;

import com.shyamsunder.placement_prep_platform.dto.AuthResponse;
import com.shyamsunder.placement_prep_platform.dto.LoginRequest;
import com.shyamsunder.placement_prep_platform.dto.RegisterRequest;
import com.shyamsunder.placement_prep_platform.dto.TokenRefreshRequest;
import com.shyamsunder.placement_prep_platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, JWT tokens, and session management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new student account", description = "Creates a new user profile, returns JWT access and refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already registered")
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Authenticate user with email and password", description = "Validates credentials and returns JWT access and refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Rotate refresh token for new access token", description = "Validates existing refresh token and issues a new access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token rotation successful"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(summary = "Logout user and revoke refresh token", description = "Invalidates the active refresh token in database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @SecurityRequirements
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}