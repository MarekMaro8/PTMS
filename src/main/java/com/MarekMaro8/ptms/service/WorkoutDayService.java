package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.PlanExercise;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.PlanExerciseRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import com.MarekMaro8.ptms.repository.WorkoutDayRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutDayService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final TrainerRepository trainerRepository;
    private final WorkoutPlanMapper workoutPlanMapper;

    public WorkoutDayService(WorkoutPlanRepository workoutPlanRepository,
                             WorkoutDayRepository workoutDayRepository,
                             PlanExerciseRepository planExerciseRepository,
                             TrainerRepository trainerRepository,
                             WorkoutPlanMapper workoutPlanMapper) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.trainerRepository = trainerRepository;
        this.workoutPlanMapper = workoutPlanMapper;
    }

    // =================================================================================
    // ODCZYT (Dostępny dla KLIENTA i TRENERA)
    // =================================================================================

    // 1. Pobierz Dzień Treningowy (Po ID)
    @Transactional(readOnly = true)
    public WorkoutDayDTO getWorkoutDayById(Long dayId, String userEmail) {
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutDay", "id", dayId));

        // Sprawdzamy czy dzwoni Właściciel lub jego Trener
        validateReadAccess(day.getWorkoutPlan(), userEmail);

        return workoutPlanMapper.toWorkoutDayDto(day);
    }

    // 2. Pobierz Wszystkie Dni z Planu (Po ID Planu)
    @Transactional(readOnly = true)
    public List<WorkoutDayDTO> getDaysByPlanId(Long planId, String userEmail) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        // Sprawdzamy czy dzwoni Właściciel lub jego Trener
        validateReadAccess(plan, userEmail);

        return workoutDayRepository.findAllByWorkoutPlanId(planId).stream()
                .map(workoutPlanMapper::toWorkoutDayDto)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // MODYFIKACJA (Tylko TRENER)
    // =================================================================================

    // 3. Dodaj Dzień do Planu
    @Transactional
    public WorkoutDayDTO createWorkoutDay(String trainerEmail, Long planId, WorkoutDayCreationDTO dayData) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        // Security: Tylko Trener tego klienta
        validateModificationAccess(plan, trainerEmail);

        WorkoutDay newWorkoutDayEntity = workoutPlanMapper.createWorkoutDayFromDto(dayData);
        plan.addWorkoutDay(newWorkoutDayEntity);

        WorkoutDay savedDay = workoutDayRepository.save(newWorkoutDayEntity);
        return workoutPlanMapper.toWorkoutDayDto(savedDay);
    }

    // 4. Dodaj Ćwiczenie do Dnia
    @Transactional
    public PlanExerciseDTO addExerciseToDay(String trainerEmail, Long dayId, PlanExerciseCreationDTO exerciseData) {
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new ResourceNotFoundException("Workoutday", "id", dayId));

        // Security: Tylko Trener tego klienta (sprawdzamy plan nadrzędny)
        validateModificationAccess(day.getWorkoutPlan(), trainerEmail);

        if (exerciseData.exerciseId() == null) {
            throw new BusinessRuleException("Wymagane ID ćwiczenia.");
        }

        PlanExercise exerciseEntity = workoutPlanMapper.createPlanExerciseFromDto(exerciseData);
        day.addPlanExercise(exerciseEntity);

        PlanExercise savedExercise = planExerciseRepository.save(exerciseEntity);
        return workoutPlanMapper.toPlanExerciseDto(savedExercise);
    }

    // =================================================================================
    // METODY POMOCNICZE (SECURITY)
    // =================================================================================

    /**
     * Sprawdza, czy użytkownik (userEmail) ma prawo ODCZYTU planu.
     * Dostęp ma: Właściciel (Klient) LUB Przypisany Trener.
     */
    private void validateReadAccess(WorkoutPlan plan, String userEmail) {
        Client owner = plan.getClient();

        boolean isOwner = owner.getEmail().equals(userEmail);
        boolean isAssignedTrainer = owner.getTrainer() != null &&
                owner.getTrainer().getEmail().equals(userEmail);

        if (!isOwner && !isAssignedTrainer) {
            throw new AccessDeniedException("Brak dostępu do tego planu treningowego.");
        }
    }


    private void validateModificationAccess(WorkoutPlan plan, String trainerEmail) {
        Client owner = plan.getClient();

        trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));

        boolean isAssignedTrainer = owner.getTrainer() != null &&
                owner.getTrainer().getEmail().equals(trainerEmail);

        if (!isAssignedTrainer) {
            throw new AccessDeniedException("Tylko przypisany trener może modyfikować ten plan.");
        }
    }
}