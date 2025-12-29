package com.MarekMaro8.ptms.dto.exercise;

import com.MarekMaro8.ptms.model.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    // Entity -> DTO (Wyświetlanie)
    public ExerciseDTO toDto(Exercise exercise) {
        if (exercise == null) return null;

        return new ExerciseDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getMuscleGroup()
        );
    }

    // CreationDTO -> Entity (Tworzenie)
    public Exercise toEntity(ExerciseCreationDTO dto) {
        if (dto == null) return null;

        return new Exercise(
                dto.getName(),
                dto.getMuscleGroup()
        );
    }
}