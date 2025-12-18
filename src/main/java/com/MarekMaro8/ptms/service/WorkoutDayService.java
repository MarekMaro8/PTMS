package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.model.PlanExercise;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.PlanExerciseRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import com.MarekMaro8.ptms.repository.WorkoutDayRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutDayService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final TrainerRepository trainerRepository; // Dodano
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

    @Transactional
    public WorkoutDayDTO createWorkoutDayWithExercises(String trainerEmail, Long planId, WorkoutDayCreationDTO dayData) {
        // Security Check
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
                .orElseThrow(() -> new IllegalArgumentException("Workout Day not found."));

        // Security Check (przez plan)
        validateTrainerAccessToPlan(trainerEmail, day.getWorkoutPlan().getId());

        if (exerciseData.getExerciseId() == null) {
            throw new IllegalArgumentException("Exercise ID cannot be empty.");
        }

        PlanExercise exerciseEntity = workoutPlanMapper.createPlanExerciseFromDto(exerciseData);
        day.addPlanExercise(exerciseEntity);
        PlanExercise savedExercise = planExerciseRepository.save(exerciseEntity);

        return workoutPlanMapper.toPlanExerciseDto(savedExercise);
    }

    @Transactional(readOnly = true)
    public List<WorkoutDayDTO> findAllByWorkoutPlanId(Long planId) {
        // Tu można zostawić publiczne lub dodać security, zależnie od potrzeb
        return workoutDayRepository.findAllByWorkoutPlanId(planId).stream()
                .map(workoutPlanMapper::toWorkoutDayDto)
                .collect(Collectors.toList());
    }

    // --- POMOCNICZE ---
    private void validateTrainerAccessToPlan(String trainerEmail, Long planId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        if (plan.getClient() == null || !plan.getClient().getTrainer().equals(trainer)) {
            throw new SecurityException("Access denied.");
        }
    }
}