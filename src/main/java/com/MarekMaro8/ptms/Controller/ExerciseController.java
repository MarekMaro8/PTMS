package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.exercise.ExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.exercise.ExerciseDTO;
import com.MarekMaro8.ptms.dto.exercise.ExerciseMapper;
import com.MarekMaro8.ptms.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService, ExerciseMapper exerciseMapper) {
        this.exerciseService = exerciseService;
    }

    @PreAuthorize("hasAnyRole('TRAINER', 'CLIENT')")
    @GetMapping()
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping()
    public ResponseEntity<ExerciseDTO> addExercise(@Valid @RequestBody ExerciseCreationDTO exerciseCreationDTO) {
        try {
            ExerciseDTO createdExercise = exerciseService.createExercise(exerciseCreationDTO);
            return ResponseEntity.ok(createdExercise);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
