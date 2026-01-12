package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.service.WorkoutPlanService;
import jakarta.validation.Valid;
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
    // DLA KLIENTA
    // =================================================================================

    // 1. Klient pobiera SWÓJ aktywny plan (Dashboard)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me/active")
    public ResponseEntity<WorkoutPlanDTO> getMyActivePlan(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyActivePlan(principal.getName()));
    }

    // 2. Klient pobiera historię SWOICH planów (Lista)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<List<WorkoutPlanDTO>> getMyPlans(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyAllPlans(principal.getName()));
    }

    // 3.  Klient pobiera szczegóły konkretnego planu (np. historycznego)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me/{planId}")
    public ResponseEntity<WorkoutPlanDTO> getMyPlanDetails(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getMyPlanById(principal.getName(), planId));
    }

    // =================================================================================
    // DLA TRENERA
    // =================================================================================

    // 1. Trener tworzy plan dla klienta
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/client/{clientId}/new")
    public ResponseEntity<WorkoutPlanDTO> createNewWorkoutPlan(
            @PathVariable Long clientId,
            @Valid @RequestBody WorkoutPlanCreationDTO newWorkoutPlan,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.createNewWorkoutPlan(principal.getName(), clientId, newWorkoutPlan));
    }

    // 2. Trener aktywuje plan
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/{planId}/activate")
    public ResponseEntity<WorkoutPlanDTO> activateWorkoutPlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.activatePlan(principal.getName(), planId));
    }

    // 3. Trener widzi WSZYSTKIE plany klienta (Historia)
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlanDTO>> getAllPlansOfClient(
            @PathVariable Long clientId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getAllPlansForClient(principal.getName(), clientId));
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