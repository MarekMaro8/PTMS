package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;
import com.MarekMaro8.ptms.service.WorkoutDayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // =================================================================================
    // ODCZYT (Klient i Trener)
    // =================================================================================

    // 1. Pobierz konkretny dzień (Szczegóły)
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @GetMapping("/{dayId}")
    public ResponseEntity<WorkoutDayDTO> getWorkoutDay(
            @PathVariable Long dayId,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.getWorkoutDayById(dayId, principal.getName())
        );
    }

    // 2. Pobierz dni z konkretnego planu (Lista dni)
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<WorkoutDayDTO>> getDaysByPlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.getDaysByPlanId(planId, principal.getName())
        );
    }

    // =================================================================================
    // MODYFIKACJA (Tylko Trener)
    // =================================================================================

    // 3. Dodaj dzień do planu
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/plan/{planId}")
    public ResponseEntity<WorkoutDayDTO> addDayToPlan(
            @PathVariable Long planId,
            @Valid @RequestBody WorkoutDayCreationDTO dayDto,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.createWorkoutDay(principal.getName(), planId, dayDto)
        );
    }

    // 4. Dodaj ćwiczenie do dnia
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/{dayId}/exercises")
    public ResponseEntity<PlanExerciseDTO> addExerciseToDay(
            @PathVariable Long dayId,
            @Valid @RequestBody PlanExerciseCreationDTO exerciseDto,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.addExerciseToDay(principal.getName(), dayId, exerciseDto)
        );
    }
}