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
import com.MarekMaro8.ptms.model.Trainer;
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
    // CZĘŚĆ 1: METODY DLA TRENERA (Tworzenie, Edycja, Podgląd Klienta)
    // =================================================================================

    @Transactional
    public WorkoutDayDTO createWorkoutDayWithExercises(String trainerEmail, Long planId, WorkoutDayCreationDTO dayData) {
        // Security: Sprawdzamy, czy plan należy do klienta tego trenera
        validateTrainerAccessToPlan(trainerEmail, planId);

        WorkoutPlan plan = workoutPlanRepository.findById(planId).orElseThrow();
        WorkoutDay newWorkoutDayEntity = workoutPlanMapper.createWorkoutDayFromDto(dayData);

        plan.addWorkoutDay(newWorkoutDayEntity);
        WorkoutDay savedDay = workoutDayRepository.save(newWorkoutDayEntity);

        return workoutPlanMapper.toWorkoutDayDto(savedDay);
    }

    @Transactional
    public PlanExerciseDTO addExerciseInstruction(String trainerEmail, Long dayId, PlanExerciseCreationDTO exerciseData) {
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new ResourceNotFoundException("Workoutday", "id", dayId));

        validateTrainerAccessToPlan(trainerEmail, day.getWorkoutPlan().getId());

        if (exerciseData.exerciseId() == null) {
            throw new BusinessRuleException("Exercise ID must be provided.");
        }

        PlanExercise exerciseEntity = workoutPlanMapper.createPlanExerciseFromDto(exerciseData);
        day.addPlanExercise(exerciseEntity);
        PlanExercise savedExercise = planExerciseRepository.save(exerciseEntity);

        return workoutPlanMapper.toPlanExerciseDto(savedExercise);
    }

    // TRENER: Pobierz konkretny dzień z planu KLIENTA
    @Transactional(readOnly = true)
    public WorkoutDayDTO getClientDayById(String trainerEmail, Long dayId) {
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutDay", "id", dayId));

        // Walidacja dostępu
        validateTrainerAccessToPlan(trainerEmail, day.getWorkoutPlan().getId());

        return workoutPlanMapper.toWorkoutDayDto(day);
    }

    // TRENER: Pobierz listę dni z planu KLIENTA
    @Transactional(readOnly = true)
    public List<WorkoutDayDTO> getClientDaysByPlanId(String trainerEmail, Long planId) {
        validateTrainerAccessToPlan(trainerEmail, planId);

        return workoutDayRepository.findAllByWorkoutPlanId(planId).stream()
                .map(workoutPlanMapper::toWorkoutDayDto)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // CZĘŚĆ 2: METODY DLA KLIENTA (Mój Profil)
    // =================================================================================

    // KLIENT: Pobierz MÓJ konkretny dzień (żeby zobaczyć szczegóły przed treningiem)
    @Transactional(readOnly = true)
    public WorkoutDayDTO getMyDayById(String clientEmail, Long dayId) {
        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutDay", "id", dayId));

        // Security: Czy ten dzień należy do mojego planu?
        if (!day.getWorkoutPlan().getClient().getEmail().equals(clientEmail)) {
            throw new AccessDeniedException("You do not have access to this workout day.");
        }

        return workoutPlanMapper.toWorkoutDayDto(day);
    }

    // KLIENT: Pobierz dni z MOJEGO planu
    @Transactional(readOnly = true)
    public List<WorkoutDayDTO> getMyDaysByPlanId(String clientEmail, Long planId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        // Security: Czy ten plan jest mój?
        if (!plan.getClient().getEmail().equals(clientEmail)) {
            throw new AccessDeniedException("You do not have access to this plan.");
        }

        return workoutDayRepository.findAllByWorkoutPlanId(planId).stream()
                .map(workoutPlanMapper::toWorkoutDayDto)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // METODY POMOCNICZE
    // =================================================================================

    private void validateTrainerAccessToPlan(String trainerEmail, Long planId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));

        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        Client planOwner = plan.getClient();

        if (planOwner == null || planOwner.getTrainer() == null || !planOwner.getTrainer().equals(trainer)) {
            throw new AccessDeniedException("You do not have access to this client's plan.");
        }
    }
}