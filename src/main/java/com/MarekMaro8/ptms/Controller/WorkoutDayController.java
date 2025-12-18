package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.service.WorkoutDayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/workout-days")
public class WorkoutDayController {

    private final WorkoutDayService workoutDayService;

    public WorkoutDayController(WorkoutDayService workoutDayService) {
        this.workoutDayService = workoutDayService;
    }

    // 1. Dodaj nowy Dzień do Planu
    @PostMapping("/plan/{planId}")
    public ResponseEntity<WorkoutDayDTO> addDayToPlan(
            @PathVariable Long planId,
            @RequestBody WorkoutDayCreationDTO dayDto,
            Principal principal) {

        WorkoutDayDTO createdDay = workoutDayService.createWorkoutDayWithExercises(principal.getName(), planId, dayDto);
        return new ResponseEntity<>(createdDay, HttpStatus.CREATED);
    }

    // 2. Dodaj instrukcję ćwiczenia do istniejącego Dnia
    @PostMapping("/{dayId}/exercises")
    public ResponseEntity<PlanExerciseDTO> addExerciseToDay(
            @PathVariable Long dayId,
            @RequestBody PlanExerciseCreationDTO exerciseDto,
            Principal principal) {

        PlanExerciseDTO createdExercise = workoutDayService.addExerciseInstruction(principal.getName(), dayId, exerciseDto);
        return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
    }

    // 3. Pobierz wszystkie dni
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<WorkoutDayDTO>> getDaysByPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(workoutDayService.findAllByWorkoutPlanId(planId));
    }
}