package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.service.WorkoutDayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/workout-days")
public class WorkoutDayController {

    private final WorkoutDayService workoutDayService;

    public WorkoutDayController(WorkoutDayService workoutDayService) {
        this.workoutDayService = workoutDayService;
    }

    // 1. Dodaj dzień do planu (Tylko Trener)
    @PostMapping("/plan/{planId}")
    public ResponseEntity<WorkoutDayDTO> addDayToPlan(
            @PathVariable Long planId,
            @RequestBody WorkoutDayCreationDTO dto,
            Principal principal) {

        // principal.getName() to email trenera
        WorkoutDayDTO createdDay = workoutDayService.addDayToPlan(principal.getName(), planId, dto);
        return new ResponseEntity<>(createdDay, HttpStatus.CREATED);
    }

    // 2. Dodaj ćwiczenie do dnia (Tylko Trener) - NOWOŚĆ
    @PostMapping("/{dayId}/exercises")
    public ResponseEntity<Void> addExerciseToDay(
            @PathVariable Long dayId,
            @RequestBody PlanExerciseCreationDTO dto,
            Principal principal) {

        workoutDayService.addExerciseToDay(principal.getName(), dayId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 3. Usuń dzień (Tylko Trener)
    @DeleteMapping("/{dayId}")
    public ResponseEntity<Void> deleteDay(
            @PathVariable Long dayId,
            Principal principal) {

        workoutDayService.deleteDay(principal.getName(), dayId);
        return ResponseEntity.noContent().build();
    }

    // 4. Usuń ćwiczenie z dnia (Tylko Trener) - Opcjonalne
    @DeleteMapping("/exercises/{planExerciseId}")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Long planExerciseId,
            Principal principal) {

        workoutDayService.deleteExerciseFromDay(principal.getName(), planExerciseId);
        return ResponseEntity.noContent().build();
    }
}