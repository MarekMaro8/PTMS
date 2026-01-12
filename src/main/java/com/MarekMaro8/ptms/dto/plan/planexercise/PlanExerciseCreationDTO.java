package com.MarekMaro8.ptms.dto.plan.planexercise;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanExerciseCreationDTO(
        @NotNull(message = "Exercise ID is required")
        Long exerciseId, // ZMIANA: ID zamiast Nazwy

        @NotNull(message = "Set number is required")
        @Min(1)
        Integer sets,

        String repsRange,

        Integer rpe

) {}