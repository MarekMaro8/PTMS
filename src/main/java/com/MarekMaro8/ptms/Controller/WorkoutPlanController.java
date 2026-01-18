package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.service.WorkoutPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    // =================================================================================
    // LISTY I STANY (Tu Trener musi podać clientId, Klient nie musi)
    // =================================================================================

    // 1. Pobierz Aktywny Plan
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @GetMapping("/active")
    public ResponseEntity<WorkoutPlanDTO> getActivePlan(
            @RequestParam(required = false) Long clientId,
            Principal principal) {

        WorkoutPlanDTO plan = workoutPlanService.getActivePlan(principal.getName(), clientId);
        if (plan == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plan);
    }

    // 2. Pobierz Historię Planów
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @GetMapping
    public ResponseEntity<List<WorkoutPlanDTO>> getAllPlans(
            @RequestParam(required = false) Long clientId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getAllPlans(principal.getName(), clientId));
    }

    // 3. Utwórz Plan (Tu Trener musi podać clientId, bo plan jeszcze nie istnieje)
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<WorkoutPlanDTO> createPlan(
            @RequestParam Long clientId,
            @Valid @RequestBody WorkoutPlanCreationDTO creationDTO,
            Principal principal) {

        WorkoutPlanDTO created = workoutPlanService.createWorkoutPlan(principal.getName(), clientId, creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =================================================================================
    // OPERACJE NA KONKRETNYM PLANIE
    // =================================================================================

    // 4. Szczegóły Planu
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @GetMapping("/{planId}")
    public ResponseEntity<WorkoutPlanDTO> getPlanDetails(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getPlanById(planId, principal.getName()));
    }

    // 5. Aktywuj Plan
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/{planId}/activate")
    public ResponseEntity<WorkoutPlanDTO> activatePlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.activatePlan(planId, principal.getName()));
    }

    // 6. Usuń Plan
    @PreAuthorize("hasRole('TRAINER')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long planId,
            Principal principal) {

        workoutPlanService.deletePlan(planId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // 4. [NOWE] Trener widzi AKTYWNY plan klienta
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<WorkoutPlanDTO> getClientActivePlan(
            @PathVariable Long clientId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getClientActivePlan(principal.getName(), clientId));
    }
}