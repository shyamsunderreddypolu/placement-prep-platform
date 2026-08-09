package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.ProblemRequest;
import com.shyamsunder.placement_prep_platform.dto.ProblemResponse;
import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemResponse addProblem(ProblemRequest request) {
        Problem problem = Problem.builder()
                .title(request.getTitle())
                .difficulty(request.getDifficulty())
                .topic(request.getTopic())
                .link(request.getLink())
                .pattern(request.getPattern())
                .build();

        Problem savedProblem = problemRepository.save(problem);
        return mapToResponse(savedProblem);
    }

    public List<ProblemResponse> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProblemResponse> getProblems(String topic, Difficulty difficulty) {
        return getProblems(topic, difficulty, null);
    }

    public List<ProblemResponse> getProblems(String topic, Difficulty difficulty, String pattern) {
        List<Problem> problems;
        if (topic != null && difficulty != null && pattern != null) {
            problems = problemRepository.findByTopicAndDifficultyAndPattern(topic, difficulty, pattern);
        } else if (topic != null && difficulty != null) {
            problems = problemRepository.findByTopicAndDifficulty(topic, difficulty);
        } else if (topic != null && pattern != null) {
            problems = problemRepository.findByTopicAndPattern(topic, pattern);
        } else if (difficulty != null && pattern != null) {
            problems = problemRepository.findByDifficultyAndPattern(difficulty, pattern);
        } else if (topic != null) {
            problems = problemRepository.findByTopic(topic);
        } else if (difficulty != null) {
            problems = problemRepository.findByDifficulty(difficulty);
        } else if (pattern != null) {
            problems = problemRepository.findByPattern(pattern);
        } else {
            problems = problemRepository.findAll();
        }

        return problems.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<String> getDistinctPatterns() {
        return problemRepository.findAllDistinctPatterns();
    }

    private ProblemResponse mapToResponse(Problem problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .topic(problem.getTopic())
                .link(problem.getLink())
                .pattern(problem.getPattern())
                .createdAt(problem.getCreatedAt())
                .build();
    }
}
