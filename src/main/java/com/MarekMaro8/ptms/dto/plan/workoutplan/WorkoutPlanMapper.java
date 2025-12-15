package com.MarekMaro8.ptms.dto.plan.workoutplan;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.model.PlanExercise;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkoutPlanMapper {


    // =================================================================================
    // 1. MAPOWANIE NA WYJŚCIE (Entity -> DTO)
    // Służy do zwracania planu (GET)
    // =================================================================================

    public WorkoutPlanDTO toDto(WorkoutPlan plan) {
        if (plan == null) {
            return null;
        }

        // Mapowanie listy Dni (z zabezpieczeniem, gdyby lista była null)
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

    // Metoda pomocnicza: Dzień Entity -> Dzień DTO ALE przyda sie tez w workoutday service :)
    public WorkoutDayDTO toWorkoutDayDto(WorkoutDay day) {
        // Mapowanie ćwiczeń wewnątrz dnia
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

    // Metoda pomocnicza: Ćwiczenie Entity -> Ćwiczenie DTO
    public PlanExerciseDTO toPlanExerciseDto(PlanExercise exercise) {
        return new PlanExerciseDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getSets(),
                exercise.getRepsRange(),
                exercise.getRpe()
        );
    }

    // =================================================================================
    // 2. MAPOWANIE NA WEJŚCIE (DTO -> Entity)
    // Służy do tworzenia planu (POST) - obsługuje "Draft" (brak dni) i pełny plan
    // =================================================================================

    public WorkoutPlan toEntity(WorkoutPlanCreationDTO dto) {
        if (dto == null) {
            return null;
        }

        WorkoutPlan plan = new WorkoutPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        // isActive jest domyślnie false w Entity, serwis decyduje o aktywacji

        // LOGIKA ELASTYCZNA:
        // Sprawdzamy, czy w DTO przyszła lista dni.
        // Jeśli jest null lub pusta -> tworzymy plan bez dni (Szkic).
        // Jeśli są dni -> tworzymy je i przypisujemy.
        if (dto.getWorkoutDays() != null && !dto.getWorkoutDays().isEmpty()) {
            for (WorkoutDayCreationDTO dayDto : dto.getWorkoutDays()) {
                WorkoutDay day = createWorkoutDayFromDto(dayDto);

                // WAŻNE: Używamy helper method z klasy WorkoutPlan,
                // aby poprawnie ustawić relację dwukierunkową (Plan <-> Dzień)
                plan.addWorkoutDay(day);
            }
        }

        return plan;
    }

    public WorkoutDay createWorkoutDayFromDto(WorkoutDayCreationDTO dayDto) {
        WorkoutDay day = new WorkoutDay();
        day.setDayName(dayDto.getDayName());
        day.setFocus(dayDto.getFocus());

        if (dayDto.getExercises() != null && !dayDto.getExercises().isEmpty()) {
            for (PlanExerciseCreationDTO exDto : dayDto.getExercises()) {
                PlanExercise exercise = createPlanExerciseFromDto(exDto);

                day.addPlanExercise(exercise);
            }
        }
        return day;
    }

    public PlanExercise createPlanExerciseFromDto(PlanExerciseCreationDTO exDto) {
        PlanExercise exercise = new PlanExercise();
        exercise.setName(exDto.getName());
        exercise.setSets(exDto.getSets());
        exercise.setRepsRange(exDto.getRepsRange());
        exercise.setRpe(exDto.getRpe());
        return exercise;
    }
}