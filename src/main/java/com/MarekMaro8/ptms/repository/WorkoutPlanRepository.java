package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    List<WorkoutPlan> findAllByClientId(Long clientId);

    Optional<WorkoutPlan> findByClientIdAndIsActiveTrue(Long clientId);
}
