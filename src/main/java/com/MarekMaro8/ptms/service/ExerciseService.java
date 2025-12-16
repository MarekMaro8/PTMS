package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO; // Możesz stworzyć dedykowane ExerciseDTO, ale na razie użyjmy encji/prostego mapowania
import com.MarekMaro8.ptms.model.Exercise;
import com.MarekMaro8.ptms.repository.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    // Pobierz wszystkie dostępne ćwiczenia (np. do listy rozwijanej na froncie)
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    // Dodaj nowe ćwiczenie do słownika (np. "Martwy Ciąg")
    @Transactional
    public Exercise createExercise(String name, String muscleGroup) {
        // Logika biznesowa: Sprawdzamy duplikaty
        if (exerciseRepository.existsByName(name)) {
            throw new IllegalArgumentException("Exercise with name '" + name + "' already exists.");
        }

        Exercise exercise = new Exercise(name, muscleGroup);
        return exerciseRepository.save(exercise);
    }

    // Pobieranie po ID (pomocnicze)
    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + id));
    }
}