package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.exercise.ExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.exercise.ExerciseDTO;
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

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // Pobierz wszystkie (Baza ćwiczeń jest wspólna, więc bez ID)
    @PreAuthorize("hasAnyRole('TRAINER', 'CLIENT')")
    @GetMapping
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    // Dodaj nowe (Nowy zasób, więc bez ID)
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<ExerciseDTO> addExercise(@Valid @RequestBody ExerciseCreationDTO exerciseCreationDTO) {
        ExerciseDTO createdExercise = exerciseService.createExercise(exerciseCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
    }
}