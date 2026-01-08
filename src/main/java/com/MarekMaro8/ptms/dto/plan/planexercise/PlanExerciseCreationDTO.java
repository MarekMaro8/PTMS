package com.MarekMaro8.ptms.dto.plan.planexercise;

import jakarta.validation.constraints.NotBlank;

public record PlanExerciseCreationDTO(
        @NotBlank(message = "Exercise name is required")
        Long exerciseId, // ZMIANA: ID zamiast Nazwy

        @NotBlank(message = "Set number is required")
        Integer sets,

        String repsRange,

        Integer rpe

) {}