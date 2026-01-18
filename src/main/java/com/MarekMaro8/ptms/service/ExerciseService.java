
package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.exercise.ExerciseCreationDTO; // Import
import com.MarekMaro8.ptms.dto.exercise.ExerciseDTO;
import com.MarekMaro8.ptms.dto.exercise.ExerciseMapper;     // Import
import com.MarekMaro8.ptms.exception.ResourceAlreadyExistsException;
import com.MarekMaro8.ptms.model.Exercise;
import com.MarekMaro8.ptms.repository.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper; // Wstrzykujemy Mapper

    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
    }

    public List<ExerciseDTO> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(exerciseMapper::toDto) // Eleganckie użycie mappera
                .collect(Collectors.toList());
    }

    @Transactional
    public ExerciseDTO createExercise(ExerciseCreationDTO creationDto) {
        if (exerciseRepository.existsByName(creationDto.name())) {
            throw new ResourceAlreadyExistsException("Exercise with name '" + creationDto.name() + "' already exists.");
        }

        Exercise exercise = exerciseMapper.toEntity(creationDto);

        Exercise saved = exerciseRepository.save(exercise);

        return exerciseMapper.toDto(saved);
    }
}
