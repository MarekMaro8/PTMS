package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.service.WorkoutPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-plans/")
public class WorkoutPlanController {


    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    // Aktualny plan treningowy klienta
    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<WorkoutPlanDTO> getActivePlanO(@PathVariable Long clientId) {
        WorkoutPlanDTO workoutPlan = workoutPlanService.getActivePlan(clientId);
        return ResponseEntity.ok(workoutPlan);
    }

    // Stworzenie nowego planu treningowego dla klienta
    @PostMapping("/client/{clientId}/new")
    public ResponseEntity<WorkoutPlanDTO> createNewWorkoutPlan(
            @PathVariable Long clientId,
            @RequestBody WorkoutPlanCreationDTO newWorkoutPlan) {
        WorkoutPlanDTO createdPlan = workoutPlanService.createNewWorkoutPlan(clientId, newWorkoutPlan);
        return ResponseEntity.ok(createdPlan);
    }


    // Aktywacja planu treningowego przez trenera
    @PostMapping("trainer/{trainerId}/{planId}/activate")
    public ResponseEntity<WorkoutPlanDTO> activateWorkoutPlan(
            @PathVariable Long trainerId,
            @PathVariable Long planId) {
        WorkoutPlanDTO activatedPlan = workoutPlanService.activatePlan(trainerId, planId);
        return ResponseEntity.ok(activatedPlan);
    }


    // Zobaczenie wszystkich planów treningowych klienta
    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlanDTO>> getAllPlansOfClient(@PathVariable Long clientId) {
        List<WorkoutPlanDTO> plans = workoutPlanService.getAllPlansForClient(clientId);
        return ResponseEntity.ok(plans);
    }
}
