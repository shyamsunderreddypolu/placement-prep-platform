package com.shyamsunder.placement_prep_platform.repository;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Submission;
import com.shyamsunder.placement_prep_platform.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(Long userId);
    List<Submission> findByUserIdOrderBySubmittedAtDesc(Long userId);
    List<Submission> findByUserIdAndStatus(Long userId, SubmissionStatus status);

    @Query("SELECT s FROM Submission s " +
           "JOIN FETCH s.problem p " +
           "WHERE s.user.id = :userId AND s.status = :status AND s.nextReviewAt IS NOT NULL AND s.nextReviewAt <= :now " +
           "ORDER BY s.nextReviewAt ASC")
    List<Submission> findDueRevisions(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT s.problem.difficulty, COUNT(DISTINCT s.problem.id) " +
           "FROM Submission s " +
           "WHERE s.user.id = :userId AND s.status = :status " +
           "GROUP BY s.problem.difficulty")
    List<Object[]> countSolvedProblemsByDifficulty(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status
    );

    @Query("SELECT s.problem.topic, COUNT(DISTINCT s.problem.id) " +
           "FROM Submission s " +
           "WHERE s.user.id = :userId AND s.status = :status " +
           "GROUP BY s.problem.topic")
    List<Object[]> countSolvedProblemsByTopic(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status
    );
}
