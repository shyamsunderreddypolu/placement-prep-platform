package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ProblemRequest;
import com.shyamsunder.placement_prep_platform.dto.ProblemResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemService problemService;

    private Problem mockProblem;

    @BeforeEach
    void setUp() {
        mockProblem = Problem.builder()
                .id(1L)
                .title("Two Sum")
                .difficulty(Difficulty.EASY)
                .topic("Arrays")
                .link("https://leetcode.com/problems/two-sum")
                .pattern("Two Pointers")
                .build();
    }

    @Test
    void addProblem_success_savesAndReturnsResponse() {
        when(problemRepository.save(any(Problem.class))).thenReturn(mockProblem);

        ProblemRequest request = ProblemRequest.builder()
                .title("Two Sum")
                .difficulty(Difficulty.EASY)
                .topic("Arrays")
                .link("https://leetcode.com/problems/two-sum")
                .pattern("Two Pointers")
                .build();

        ProblemResponse response = problemService.addProblem(request);

        assertNotNull(response);
        assertEquals("Two Sum", response.getTitle());
        assertEquals("Two Pointers", response.getPattern());
        verify(problemRepository, times(1)).save(any(Problem.class));
    }

    @Test
    void getProblems_filterByPattern_returnsMatchingProblems() {
        when(problemRepository.findByPattern("Two Pointers")).thenReturn(List.of(mockProblem));

        List<ProblemResponse> results = problemService.getProblems(null, null, "Two Pointers");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Two Pointers", results.get(0).getPattern());
        verify(problemRepository, times(1)).findByPattern("Two Pointers");
    }

    @Test
    void getDistinctPatterns_returnsListOfPatternNames() {
        List<String> mockPatterns = List.of("Binary Search", "Sliding Window", "Two Pointers");
        when(problemRepository.findAllDistinctPatterns()).thenReturn(mockPatterns);

        List<String> patterns = problemService.getDistinctPatterns();

        assertNotNull(patterns);
        assertEquals(3, patterns.size());
        assertTrue(patterns.contains("Two Pointers"));
        verify(problemRepository, times(1)).findAllDistinctPatterns();
    }
}
