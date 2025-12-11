package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
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

    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<WorkoutPlan> getActivePlanO(@PathVariable Long clientId) {
        WorkoutPlan workoutPlan = workoutPlanService.getActivePlan(clientId);
        return ResponseEntity.ok(workoutPlan);
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<WorkoutPlan> createNewWorkoutPlan(
            @PathVariable Long clientId,
            @RequestBody WorkoutPlan newWorkoutPlan) {
        WorkoutPlan createdPlan = workoutPlanService.createNewWorkoutPlan(clientId, newWorkoutPlan);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlan>> getAllPlansOfClient(@PathVariable Long clientId) {
        List<WorkoutPlan> plans = workoutPlanService.getAllPlansOfClient(clientId);
        return ResponseEntity.ok(plans);
    }
}
