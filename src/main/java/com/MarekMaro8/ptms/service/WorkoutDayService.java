package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.model.PlanExercise;
import com.MarekMaro8.ptms.model.SessionExercise;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.PlanExerciseRepository;
import com.MarekMaro8.ptms.repository.SessionExerciseRepository;
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
    private final PlanExerciseRepository planExerciseRepository; // 2. Potrzebne repozytorium
    private final WorkoutPlanMapper workoutPlanMapper;
    private final SessionExerciseRepository sessionExerciseRepository;

    public WorkoutDayService(WorkoutPlanRepository workoutPlanRepository,
                             WorkoutDayRepository workoutDayRepository,
                             PlanExerciseRepository planExerciseRepository, // 3. Wstrzykiwanie
                             WorkoutPlanMapper workoutPlanMapper, SessionExerciseRepository sessionExerciseRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.workoutPlanMapper = workoutPlanMapper;
        this.sessionExerciseRepository = sessionExerciseRepository;
    }

    // tworzenie dnia treningowego w planie
    @Transactional
    public WorkoutDayDTO createWorkoutDayWithExercises(Long planId, WorkoutDayCreationDTO dayData) {

        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Plan not found."));

        WorkoutDay newWorkoutDayEntity = workoutPlanMapper.createWorkoutDayFromDto(dayData);

        plan.addWorkoutDay(newWorkoutDayEntity);

        WorkoutDay savedDay = workoutDayRepository.save(newWorkoutDayEntity);

        return workoutPlanMapper.toWorkoutDayDto(savedDay);
    }

    // dodawanie ćwiczenia do dnia treningowego
    @Transactional
    public PlanExerciseDTO addExerciseInstruction(Long dayId, PlanExerciseCreationDTO exerciseData) {

        WorkoutDay day = workoutDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Day not found."));

        if (exerciseData.getExerciseId() == null) {
            throw new IllegalArgumentException("Exercise name cannot be empty.");
        }

        PlanExercise exerciseEntity = workoutPlanMapper.createPlanExerciseFromDto(exerciseData);

        day.addPlanExercise(exerciseEntity);

        PlanExercise savedExercise = planExerciseRepository.save(exerciseEntity);

        return workoutPlanMapper.toPlanExerciseDto(savedExercise);
    }


    // pobieranie dni treningowych dla planu
    @Transactional(readOnly = true) // Tylko do odczytu
    public List<WorkoutDayDTO> findAllByWorkoutPlanId(Long planId) {
        List<WorkoutDay> days = workoutDayRepository.findAllByWorkoutPlanId(planId);
        return days.stream()
                .map(workoutPlanMapper::toWorkoutDayDto)
                .collect(Collectors.toList());
    }

    // usuwanie ćwiczenia z sesji
    @Transactional
    public void deleteSessionExercise(Long sessionId, Long sessionExerciseId) {
        SessionExercise exercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        if (!exercise.getSession().getId().equals(sessionId)) {
            throw new SecurityException("Exercise does not belong to this session");
        }

        sessionExerciseRepository.delete(exercise);
    }


}