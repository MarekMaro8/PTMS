package com.MarekMaro8.ptms.dto.session;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SessionExerciseDTO (

    Long id,

    // Dane ze słownika (Spłaszczone dla wygody Frontendu)
    Long exerciseId,

    @NotBlank(message = "Muscle name is required")
    String exerciseName,

    @NotBlank(message = "Muscle group is required")
    String muscleGroup,  // Opcjonalnie, żeby np. pokazać ikonkę "Nogi"

    Integer orderIndex,
    // Dane z wykonaniaInteger orderIndex,
    String notes,

    // Lista dzieci (Serie)
    List<SessionSetDTO> sets

){}