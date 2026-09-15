package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.config.JwtService;
import com.shyamsunder.placement_prep_platform.dto.AuthResponse;
import com.shyamsunder.placement_prep_platform.dto.LoginRequest;
import com.shyamsunder.placement_prep_platform.dto.RegisterRequest;
import com.shyamsunder.placement_prep_platform.dto.TokenRefreshRequest;
import com.shyamsunder.placement_prep_platform.entity.RefreshToken;
import com.shyamsunder.placement_prep_platform.entity.Role;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.DuplicateResourceException;
import com.shyamsunder.placement_prep_platform.exception.UnauthorizedException;
import com.shyamsunder.placement_prep_platform.repository.RefreshTokenRepository;
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

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

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
        when(jwtService.getRefreshExpiration()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> {
            RefreshToken rt = i.getArgument(0);
            rt.setId(1L);
            return rt;
        });

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mockJwtTokenHeaderPayloadSignature", response.getToken());
        assertEquals("student@test.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());
        assertNotNull(response.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("securePassword123");
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        User existing = User.builder().id(1L).email("student@test.com").build();
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_success_validatesCredentialsAndReturnsJwt() {
        User existingUser = User.builder()
                .id(1L)
                .name("New Student")
                .email("student@test.com")
                .passwordHash("encodedPasswordHash")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(existingUser)).thenReturn("mockJwtTokenHeaderPayloadSignature");
        when(jwtService.getRefreshExpiration()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> {
            RefreshToken rt = i.getArgument(0);
            rt.setId(1L);
            return rt;
        });

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockJwtTokenHeaderPayloadSignature", response.getToken());
        assertEquals("student@test.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());

        verify(authenticationManager, times(1)).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
    }

    @Test
    void refreshToken_success_returnsNewAccessToken() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("student@test.com")
                .role(Role.ROLE_USER)
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token("valid-refresh-token")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateToken(user)).thenReturn("new-access-token");

        TokenRefreshRequest refreshRequest = TokenRefreshRequest.builder().refreshToken("valid-refresh-token").build();
        AuthResponse response = authService.refreshToken(refreshRequest);

        assertNotNull(response);
        assertEquals("new-access-token", response.getToken());
        assertEquals("valid-refresh-token", response.getRefreshToken());
    }
}
