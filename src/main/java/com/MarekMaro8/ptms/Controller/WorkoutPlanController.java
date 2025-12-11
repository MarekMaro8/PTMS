package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
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
    public ResponseEntity<WorkoutPlanDTO> getActivePlanO(@PathVariable Long clientId) {
        WorkoutPlanDTO workoutPlan = workoutPlanService.getActivePlan(clientId);
        return ResponseEntity.ok(workoutPlan);
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<WorkoutPlanDTO> createNewWorkoutPlan(
            @PathVariable Long clientId,
            @RequestBody WorkoutPlanCreationDTO newWorkoutPlan) {
        WorkoutPlanDTO createdPlan = workoutPlanService.createNewWorkoutPlan(clientId, newWorkoutPlan);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlanDTO>> getAllPlansOfClient(@PathVariable Long clientId) {
        List<WorkoutPlanDTO> plans = workoutPlanService.getAllPlansForClient(clientId);
        return ResponseEntity.ok(plans);
    }
}
