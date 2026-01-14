package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    List<WorkoutPlan> findAllByClientId(Long clientId);

    Optional<WorkoutPlan> findByClientIdAndIsActiveTrue(Long clientId);

    @Query("SELECT p FROM WorkoutPlan p LEFT JOIN FETCH p.workoutDays WHERE p.client.id = :clientId")
    List<WorkoutPlan> findAllByClientIdWithDays(@Param("clientId") Long clientId);
}
