package com.shyamsunder.placement_prep_platform.repository;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByTopic(String topic);
    List<Problem> findByDifficulty(Difficulty difficulty);
    List<Problem> findByTopicAndDifficulty(String topic, Difficulty difficulty);
}
