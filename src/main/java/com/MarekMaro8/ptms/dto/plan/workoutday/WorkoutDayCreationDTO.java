package com.MarekMaro8.ptms.dto.plan.workoutday;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record WorkoutDayCreationDTO(
        @NotBlank(message = "Day name is required")
        String dayName,

        @NotBlank(message = "Focus is required")
        String focus,

        // Lista ćwiczeń do stworzenia w tym dniu
        List<PlanExerciseCreationDTO> exercises
) {}

