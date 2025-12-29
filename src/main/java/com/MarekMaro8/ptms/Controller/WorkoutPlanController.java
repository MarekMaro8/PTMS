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

    // --- DLA KLIENTA ---

    // Klient pobiera SWÓJ aktywny plan
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me/active")
    public ResponseEntity<WorkoutPlanDTO> getMyActivePlan(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyActivePlan(principal.getName()));
    }

    // Klient pobiera historię SWOICH planów
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<List<WorkoutPlanDTO>> getMyPlans(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyAllPlans(principal.getName()));
    }

    // --- DLA TRENERA ---

    // Trener tworzy plan dla klienta
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/client/{clientId}/new")
    public ResponseEntity<WorkoutPlanDTO> createNewWorkoutPlan(
            @PathVariable Long clientId,
            @Valid  @RequestBody WorkoutPlanCreationDTO newWorkoutPlan,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.createNewWorkoutPlan(principal.getName(), clientId, newWorkoutPlan));
    }

    // Trener aktywuje plan
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/{planId}/activate")
    public ResponseEntity<WorkoutPlanDTO> activateWorkoutPlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.activatePlan(principal.getName(), planId));
    }

    // Trener widzi plany klienta
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlanDTO>> getAllPlansOfClient(
            @PathVariable Long clientId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getAllPlansForClient(principal.getName(), clientId));
    }
}