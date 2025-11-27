package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.model.PlanExercise;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.PlanExerciseRepository;
import com.MarekMaro8.ptms.repository.WorkoutDayRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkoutDayService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final PlanExerciseRepository planExerciseRepository;

    public WorkoutDayService(WorkoutPlanRepository workoutPlanRepository, WorkoutDayRepository workoutDayRepository, PlanExerciseRepository planExerciseRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.planExerciseRepository = planExerciseRepository;
    }


    @Transactional
    public WorkoutDay createWorkoutDayWithExercises(Long planId, WorkoutDay dayData, List<PlanExercise> exercisesData) {

        // 1. POBRANIE: Sprawdź, czy Plan istnieje
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Plan not found."));

        // 2. TWORZENIE DNIA I SYNCHRONIZACJA
        plan.addWorkoutDay(dayData); // Zapewnia plan.setWorkoutPlan(this)

        // 3. Zapis Dnia (żeby miał ID przed dodaniem do niego ćwiczeń)
        WorkoutDay savedDay = workoutDayRepository.save(dayData);

        // 4. DODAWANIE ĆWICZEŃ
        for (PlanExercise exercise : exercisesData) {
            addExerciseInstruction(savedDay.getId(), exercise);
        }

        return savedDay;
    }

    /**
     * Tworzy instrukcję ćwiczenia (PlanExercise).
     */
    @Transactional
    public PlanExercise addExerciseInstruction(Long dayId, PlanExercise exerciseData) {

        // 1. POBRANIE: Upewnij się, że Dzień istnieje
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Day not found."));

        // 2. WALIDACJA LOKALNA: Nazwa ćwiczenia jest wymagana.
        if (exerciseData.getName() == null || exerciseData.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Exercise name cannot be empty.");
        }

        // 3. SYNCHRONIZACJA RELACJI (Helper Method):
        // Użycie helper method z klasy WorkoutDay, by ustawić FK w PlanExercise
        day.addPlanExercise(exerciseData);

        // 4. ZAPIS:
        return planExerciseRepository.save(exerciseData);
    }
}