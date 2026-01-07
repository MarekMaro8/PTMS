package com.MarekMaro8.ptms.dto.plan.workoutplan;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.model.Exercise;
import com.MarekMaro8.ptms.model.PlanExercise;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ExerciseRepository; // Importujemy Twoje repozytorium!
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkoutPlanMapper {

    private final ExerciseRepository exerciseRepository;

    // Wstrzykujemy ExerciseRepository przez konstruktor
    public WorkoutPlanMapper(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    // =================================================================================
    // 1. MAPOWANIE NA WYJŚCIE (Entity -> DTO)
    // =================================================================================

    public WorkoutPlanDTO toDto(WorkoutPlan plan) {
        if (plan == null) return null;

        List<WorkoutDayDTO> daysDto = new ArrayList<>();
        if (plan.getWorkoutDays() != null) {
            daysDto = plan.getWorkoutDays().stream()
                    .map(this::toWorkoutDayDto)
                    .collect(Collectors.toList());
        }

        Long clientId = (plan.getClient() != null) ? plan.getClient().getId() : null;

        return new WorkoutPlanDTO(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getIsActive(),
                clientId,
                daysDto
        );
    }

    public WorkoutDayDTO toWorkoutDayDto(WorkoutDay day) {
        List<PlanExerciseDTO> exercisesDto = new ArrayList<>();
        if (day.getPlanExercises() != null) {
            exercisesDto = day.getPlanExercises().stream()
                    .map(this::toPlanExerciseDto)
                    .collect(Collectors.toList());
        }

        return new WorkoutDayDTO(
                day.getId(),
                day.getDayName(),
                day.getFocus(),
                exercisesDto
        );
    }

    public PlanExerciseDTO toPlanExerciseDto(PlanExercise exercise) {
        // Zabezpieczenie: czy ćwiczenie ma przypisany obiekt ze słownika?
        Long exId = (exercise.getExercise() != null) ? exercise.getExercise().getId() : null;
        String exName = (exercise.getExercise() != null) ? exercise.getExercise().getName() : "Unknown Exercise";

        return new PlanExerciseDTO(
                exercise.getId(),
                exId,       // ID ze słownika
                exName,     // Nazwa ze słownika
                exercise.getSets(),
                exercise.getRepsRange(),
                exercise.getRpe()
        );
    }

    // =================================================================================
    // 2. MAPOWANIE NA WEJŚCIE (DTO -> Entity)
    // =================================================================================

    public WorkoutPlan toEntity(WorkoutPlanCreationDTO dto) {
        if (dto == null) return null;

        WorkoutPlan plan = new WorkoutPlan();
        plan.setName(dto.name());
        plan.setDescription(dto.description());

        if (dto.workoutDays() != null && !dto.workoutDays().isEmpty()) {
            for (WorkoutDayCreationDTO dayDto : dto.workoutDays()) {
                WorkoutDay day = createWorkoutDayFromDto(dayDto);
                plan.addWorkoutDay(day);
            }
        }
        return plan;
    }

    public WorkoutDay createWorkoutDayFromDto(WorkoutDayCreationDTO dayDto) {
        WorkoutDay day = new WorkoutDay();
        day.setDayName(dayDto.dayName());
        day.setFocus(dayDto.focus());

        if (dayDto.exercises() != null && !dayDto.exercises().isEmpty()) {
            for (PlanExerciseCreationDTO exDto : dayDto.exercises()) {
                PlanExercise exercise = createPlanExerciseFromDto(exDto);
                day.addPlanExercise(exercise);
            }
        }
        return day;
    }

    // --- KLUCZOWA ZMIANA TUTAJ ---
    public PlanExercise createPlanExerciseFromDto(PlanExerciseCreationDTO planExerciseCreationDTO) {
        PlanExercise planExercise = new PlanExercise();

        // 1. Pobieramy ćwiczenie z bazy na podstawie ID przesłanego z frontendu
        Exercise exerciseDict = exerciseRepository.findById(planExerciseCreationDTO.exerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + planExerciseCreationDTO.exerciseId()));

        // 2. Przypisujemy obiekt Exercise do PlanExercise
        planExercise.setExercise(exerciseDict);

        // 3. Reszta parametrów
        planExercise.setSets(planExerciseCreationDTO.sets());
        planExercise.setRepsRange(planExerciseCreationDTO.repsRange());
        planExercise.setRpe(planExerciseCreationDTO.rpe());

        return planExercise;
    }
}