package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.model.*;
import com.MarekMaro8.ptms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutDayService {
    private final WorkoutDayRepository workoutDayRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final TrainerRepository trainerRepository;
    private final ExerciseRepository exerciseRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final WorkoutPlanMapper workoutPlanMapper;

    public WorkoutDayService(WorkoutDayRepository workoutDayRepository,
                             WorkoutPlanRepository workoutPlanRepository,
                             TrainerRepository trainerRepository,
                             ExerciseRepository exerciseRepository,
                             PlanExerciseRepository planExerciseRepository, WorkoutPlanMapper workoutPlanMapper
                             ) {
        this.workoutDayRepository = workoutDayRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.trainerRepository = trainerRepository;
        this.exerciseRepository = exerciseRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.workoutPlanMapper = workoutPlanMapper;
    }

    // --- DODAWANIE DNIA DO PLANU ---
    @Transactional
    public WorkoutDayDTO addDayToPlan(String trainerEmail, Long planId, WorkoutDayCreationDTO creationDTO) {
        // 1. Sprawdź czy to Twój klient
        WorkoutPlan plan = validateTrainerAccessToPlan(trainerEmail, planId);

        WorkoutDay workoutDay = workoutPlanMapper.createWorkoutDayFromDto(creationDTO);
        workoutDay.setWorkoutPlan(plan); // Ustaw relację

        // Jeśli logika biznesowa wymaga od razu zapisania pustego dnia:
        WorkoutDay savedDay = workoutDayRepository.save(workoutDay);
        return workoutPlanMapper.toWorkoutDayDto(savedDay);
    }

    // --- DODAWANIE ĆWICZENIA DO DNIA ---
    @Transactional
    public void addExerciseToDay(String trainerEmail, Long dayId, PlanExerciseCreationDTO dto) {
        // 1. Pobierz dzień
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout day not found"));

        // 2. Sprawdź czy masz prawo edytować ten dzień (poprzez plan i klienta)
        validateTrainerAccessToPlan(trainerEmail, day.getWorkoutPlan().getId());

        // 3. Pobierz definicję ćwiczenia ze słownika
        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found in dictionary"));

        // 4. Stwórz obiekt PlanExercise (Konkretne zalecenie w planie)
        PlanExercise planExercise = new PlanExercise();
        planExercise.setWorkoutDay(day);
        planExercise.setExercise(exercise);
        planExercise.setSets(dto.getSets());
        planExercise.setRepsRange(dto.getRepsRange());

        // 5. Zapisz
        planExerciseRepository.save(planExercise);
    }

    // --- USUWANIE DNIA ---
    @Transactional
    public void deleteDay(String trainerEmail, Long dayId) {
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout day not found"));

        // Security Check
        validateTrainerAccessToPlan(trainerEmail, day.getWorkoutPlan().getId());

        workoutDayRepository.delete(day);
    }

    // --- USUWANIE ĆWICZENIA Z DNIA (Opcjonalne, ale warto mieć) ---
    @Transactional
    public void deleteExerciseFromDay(String trainerEmail, Long planExerciseId) {
        PlanExercise planExercise = planExerciseRepository.findById(planExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Plan exercise not found"));

        Long planId = planExercise.getWorkoutDay().getWorkoutPlan().getId();
        validateTrainerAccessToPlan(trainerEmail, planId);

        planExerciseRepository.delete(planExercise);
    }

    // --- METODA POMOCNICZA (SECURITY) ---
    private WorkoutPlan validateTrainerAccessToPlan(String trainerEmail, Long planId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // Czy plan należy do klienta, który należy do tego trenera?
        if (plan.getClient() == null ||
                plan.getClient().getTrainer() == null ||
                !plan.getClient().getTrainer().getId().equals(trainer.getId())) {

            throw new SecurityException("Access denied: You are not the trainer of this client.");
        }
        return plan;
    }
}