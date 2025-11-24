package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.WorkoutDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutDayRepository extends JpaRepository<WorkoutDay, Long> {
    List<WorkoutDay> findAllByWorkoutPlanId(Long planId);
}
