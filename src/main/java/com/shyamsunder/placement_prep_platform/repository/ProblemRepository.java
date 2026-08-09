package com.shyamsunder.placement_prep_platform.repository;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByTopic(String topic);
    List<Problem> findByDifficulty(Difficulty difficulty);
    List<Problem> findByPattern(String pattern);
    List<Problem> findByTopicAndDifficulty(String topic, Difficulty difficulty);
    List<Problem> findByTopicAndPattern(String topic, String pattern);
    List<Problem> findByDifficultyAndPattern(Difficulty difficulty, String pattern);
    List<Problem> findByTopicAndDifficultyAndPattern(String topic, Difficulty difficulty, String pattern);

    @Query("SELECT DISTINCT p.pattern FROM Problem p WHERE p.pattern IS NOT NULL ORDER BY p.pattern ASC")
    List<String> findAllDistinctPatterns();
}
