package com.MarekMaro8.ptms.dto.exercise;

import jakarta.validation.constraints.NotBlank;

public record ExerciseCreationDTO(

        @NotBlank(message = "Exercise name is required")
        String name,

        @NotBlank(message = "Muscle group is required")
        String muscleGroup,

        @NotBlank(message = "Description is required")
        String description
) {
}