package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.service.WorkoutPlanService;
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

    // =========================================================
    // 1. ENDPOINTY DLA KLIENTA (Context: ME)
    // =========================================================

    // Klient sprawdza, co ma ćwiczyć
    @GetMapping("/me/active")
    public ResponseEntity<WorkoutPlanDTO> getMyActivePlan(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyActivePlan(principal.getName()));
    }

    // Klient przegląda historię swoich planów
    @GetMapping("/me")
    public ResponseEntity<List<WorkoutPlanDTO>> getMyPlans(Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getMyAllPlans(principal.getName()));
    }

    // =========================================================
    // 2. ENDPOINTY DLA TRENERA (Context: CLIENT ID)
    // =========================================================

    // Trener podgląda aktywny plan klienta
    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<WorkoutPlanDTO> getClientActivePlan(
            @PathVariable Long clientId,
            Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getClientActivePlan(principal.getName(), clientId));
    }

    // Trener pobiera listę wszystkich planów klienta
    @GetMapping("/client/{clientId}/all")
    public ResponseEntity<List<WorkoutPlanDTO>> getClientPlans(
            @PathVariable Long clientId,
            Principal principal) {
        return ResponseEntity.ok(workoutPlanService.getClientPlans(principal.getName(), clientId));
    }

    // Trener tworzy nowy plan dla klienta

    @PostMapping("/client/{clientId}/new")
    public ResponseEntity<WorkoutPlanDTO> createPlanForClient(
            @PathVariable Long clientId,
            @RequestBody WorkoutPlanCreationDTO dto,
            Principal principal) {

        WorkoutPlanDTO createdPlan = workoutPlanService.createPlanForClient(principal.getName(), clientId, dto);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    // Trener aktywuje stary plan (lub inny wybrany)
    @PostMapping("/{planId}/activate")
    public ResponseEntity<WorkoutPlanDTO> activatePlan(
            @PathVariable Long planId,
            Principal principal) {

        return ResponseEntity.ok(workoutPlanService.activatePlan(principal.getName(), planId));
    }
}