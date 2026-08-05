package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.config.JwtService;
import com.shyamsunder.placement_prep_platform.dto.AuthResponse;
import com.shyamsunder.placement_prep_platform.dto.LoginRequest;
import com.shyamsunder.placement_prep_platform.dto.RegisterRequest;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("New Student")
                .email("student@test.com")
                .password("securePassword123")
                .branch("CSE")
                .graduationYear(2026)
                .build();

        loginRequest = LoginRequest.builder()
                .email("student@test.com")
                .password("securePassword123")
                .build();
    }

    @Test
    void register_success_encodesPasswordAndGeneratesJwt() {
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securePassword123")).thenReturn("encodedPasswordHash");
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtTokenHeaderPayloadSignature");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mockJwtTokenHeaderPayloadSignature", response.getToken());
        assertEquals("student@test.com", response.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("securePassword123");
    }

    @Test
    void register_duplicateEmail_throwsIllegalArgumentException() {
        User existing = User.builder().id(1L).email("student@test.com").build();
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_success_validatesCredentialsAndReturnsJwt() {
        User existingUser = User.builder()
                .id(1L)
                .name("New Student")
                .email("student@test.com")
                .passwordHash("encodedPasswordHash")
                .build();

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(existingUser)).thenReturn("mockJwtTokenHeaderPayloadSignature");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockJwtTokenHeaderPayloadSignature", response.getToken());
        assertEquals("student@test.com", response.getEmail());

        verify(authenticationManager, times(1)).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
    }
}
