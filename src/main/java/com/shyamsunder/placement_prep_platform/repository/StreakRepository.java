package com.shyamsunder.placement_prep_platform.repository;

import com.shyamsunder.placement_prep_platform.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {
    Optional<Streak> findByUserId(Long userId);
}
