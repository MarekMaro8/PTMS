package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.service.WorkoutPlanService;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/me/active")
    public ResponseEntity<WorkoutPlanDTO> getMyActivePlan(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyActivePlan(principal.getName()));
    }

    // Klient pobiera historię SWOICH planów
    @GetMapping("/me")
    public ResponseEntity<List<WorkoutPlanDTO>> getMyPlans(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyAllPlans(principal.getName()));
    }

    // --- DLA TRENERA ---

    // Trener tworzy plan dla klienta
    @PostMapping("/client/{clientId}/new")
    public ResponseEntity<WorkoutPlanDTO> createNewWorkoutPlan(
            @PathVariable Long clientId,
            @RequestBody WorkoutPlanCreationDTO newWorkoutPlan,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.createNewWorkoutPlan(principal.getName(), clientId, newWorkoutPlan));
    }

    // Trener aktywuje plan
    @PostMapping("/{planId}/activate")
    public ResponseEntity<WorkoutPlanDTO> activateWorkoutPlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.activatePlan(principal.getName(), planId));
    }

    // Trener widzi plany klienta
    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlanDTO>> getAllPlansOfClient(
            @PathVariable Long clientId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.getAllPlansForClient(principal.getName(), clientId));
    }
}