package com.MarekMaro8.ptms.dto.plan.planexercise;

public record PlanExerciseDTO(
        Long id,
        Long exerciseId, // ID ze słownika
        String name,     // Nazwa ze słownika (do wyświetlania)
        Integer sets,
        String repsRange,
        Integer rpe
) {}