package com.MarekMaro8.ptms.dto.plan.workoutday;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;

import java.util.List;

public record WorkoutDayDTO (
     Long id,
     String dayName,
     String focus,
     List<PlanExerciseDTO> exercises
){}