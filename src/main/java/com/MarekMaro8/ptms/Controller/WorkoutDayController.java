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
    // DLA TRENERA (Zarządzanie)
    // =================================================================================

    // 1. Dodaj dzień do planu
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/plan/{planId}")
    public ResponseEntity<WorkoutDayDTO> addDayToPlan(
            @PathVariable Long planId,
            @Valid @RequestBody WorkoutDayCreationDTO dayDto,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.createWorkoutDayWithExercises(principal.getName(), planId, dayDto)
        );
    }

    // 2. Dodaj ćwiczenie do dnia
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/{dayId}/exercises")
    public ResponseEntity<PlanExerciseDTO> addExerciseToDay(
            @PathVariable Long dayId,
            @Valid @RequestBody PlanExerciseCreationDTO exerciseDto,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.addExerciseInstruction(principal.getName(), dayId, exerciseDto)
        );
    }

    // 3. Pobierz konkretny dzień klienta (Podgląd)
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/client-view/{dayId}")
    public ResponseEntity<WorkoutDayDTO> getClientDay(
            @PathVariable Long dayId,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.getClientDayById(principal.getName(), dayId)
        );
    }

    // 4. Pobierz wszystkie dni z planu (Podgląd planu)
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/plan/{planId}/all")
    public ResponseEntity<List<WorkoutDayDTO>> getClientDaysFromPlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.getClientDaysByPlanId(principal.getName(), planId)
        );
    }

    // =================================================================================
    // DLA KLIENTA (Mój Trening)
    // =================================================================================

    // 1. Pobierz MÓJ konkretny dzień (Szczegóły przed startem)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me/{dayId}")
    public ResponseEntity<WorkoutDayDTO> getMyDay(
            @PathVariable Long dayId,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.getMyDayById(principal.getName(), dayId)
        );
    }

    // 2. Pobierz dni z MOJEGO planu (Widok kalendarza/listy)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me/plan/{planId}")
    public ResponseEntity<List<WorkoutDayDTO>> getMyDaysFromPlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(
                workoutDayService.getMyDaysByPlanId(principal.getName(), planId)
        );
    }
}