package com.shyamsunder.placement_prep_platform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyamsunder.placement_prep_platform.dto.ProblemRequest;
import com.shyamsunder.placement_prep_platform.dto.ReviewFeedbackRequest;
import com.shyamsunder.placement_prep_platform.dto.TokenRefreshRequest;
import com.shyamsunder.placement_prep_platform.entity.*;
import com.shyamsunder.placement_prep_platform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OwnershipSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User alice;
    private User bob;
    private Problem sampleProblem;
    private Resume aliceResume;
    private Submission aliceSubmission;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(User.builder()
                .name("Alice")
                .email("alice@test.com")
                .passwordHash("hashedpass")
                .branch("Computer Science")
                .graduationYear(2025)
                .role(Role.ROLE_USER)
                .build());

        bob = userRepository.save(User.builder()
                .name("Bob")
                .email("bob@test.com")
                .passwordHash("hashedpass")
                .branch("Information Technology")
                .graduationYear(2025)
                .role(Role.ROLE_USER)
                .build());

        sampleProblem = problemRepository.save(Problem.builder()
                .title("Binary Search")
                .difficulty(Difficulty.EASY)
                .topic("Binary Search")
                .link("https://leetcode.com/problems/binary-search")
                .pattern("Binary Search")
                .build());

        aliceResume = resumeRepository.save(Resume.builder()
                .user(alice)
                .fileName("alice_resume.pdf")
                .fileUrl("alice_uuid.pdf")
                .uploadedAt(LocalDateTime.now())
                .build());

        aliceSubmission = submissionRepository.save(Submission.builder()
                .user(alice)
                .problem(sampleProblem)
                .status(SubmissionStatus.SOLVED)
                .notes("Clean binary search")
                .nextReviewAt(LocalDateTime.now().minusHours(1))
                .build());
    }

    @Test
    @WithMockUser(username = "bob@test.com", roles = "USER")
    void downloadResume_whenNotOwner_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/resumes/" + aliceResume.getId() + "/download"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Access denied: You do not own this resume"));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void addProblem_whenRoleUser_returnsForbidden() throws Exception {
        ProblemRequest request = ProblemRequest.builder()
                .title("Unauthorized Problem")
                .difficulty(Difficulty.HARD)
                .topic("Graphs")
                .link("https://leetcode.com/problems/course-schedule")
                .pattern("Topological Sort")
                .build();

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@placementprep.com", roles = "ADMIN")
    void addProblem_whenRoleAdmin_returnsCreated() throws Exception {
        ProblemRequest request = ProblemRequest.builder()
                .title("Admin Created Problem")
                .difficulty(Difficulty.MEDIUM)
                .topic("Trees")
                .link("https://leetcode.com/problems/invert-binary-tree")
                .pattern("DFS")
                .build();

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Admin Created Problem"))
                .andExpect(jsonPath("$.topic").value("Trees"));
    }

    @Test
    @WithMockUser(username = "bob@test.com", roles = "USER")
    void submitReview_whenNotOwner_returnsForbidden() throws Exception {
        ReviewFeedbackRequest request = ReviewFeedbackRequest.builder()
                .feedback(ReviewFeedback.EASY)
                .build();

        mockMvc.perform(post("/api/revisions/" + aliceSubmission.getId() + "/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Access denied: You do not own this submission"));
    }

    @Test
    void refreshToken_whenTokenNotFound_returnsBadRequest() throws Exception {
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("non-existent-token")
                .build();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void refreshToken_whenTokenRevoked_returnsUnauthorized() throws Exception {
        RefreshToken revokedToken = refreshTokenRepository.save(RefreshToken.builder()
                .user(alice)
                .token("revoked-token-uuid-123")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(true)
                .build());

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken(revokedToken.getToken())
                .build();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Refresh token was expired or revoked. Please log in again."));
    }
}