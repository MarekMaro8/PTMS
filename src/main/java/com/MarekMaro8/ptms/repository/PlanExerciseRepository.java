package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {
    List<PlanExercise> findAllByWorkoutDayId(Long dayId);
}