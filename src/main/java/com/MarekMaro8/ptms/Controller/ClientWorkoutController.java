package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.session.*;
import com.MarekMaro8.ptms.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.security.Principal;

@RestController
// USUWAMY {clientId} z prefiksu!
@RequestMapping("/api/workouts")
public class ClientWorkoutController {

    private final SessionService sessionService;

    public ClientWorkoutController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // Start
    @PostMapping("/start/{workoutDayId}")
    public ResponseEntity<SessionDTO> startWorkout(
            @PathVariable Long workoutDayId,
            @RequestBody SessionStartDTO startDto,
            Principal principal) {

        SessionDTO newSession = sessionService.startSession(principal.getName(), workoutDayId, startDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
    }

    // Finish
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<SessionDTO> finishWorkout(
            @PathVariable Long sessionId,
            Principal principal) {

        SessionDTO completedSession = sessionService.completeSession(sessionId, principal.getName());
        return ResponseEntity.ok(completedSession);
    }

    // Update Notes
    @PatchMapping("/{sessionId}/notes")
    public ResponseEntity<Void> updateNotes(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body,
            Principal principal) {

        // Wyciągamy wartość po kluczu "notes"
        String notes = body.get("notes");
        sessionService.updateSessionNotes(sessionId, principal.getName(), notes);
        return ResponseEntity.ok().build();
    }

    // Add Set
    @PostMapping("/{sessionId}/exercises/{sessionExerciseId}/sets")
    public ResponseEntity<SessionDTO> addSet(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @RequestBody SessionSetDTO setDto,
            Principal principal) {

        SessionDTO updatedSession = sessionService.addSetToExercise(sessionId, sessionExerciseId, setDto, principal.getName());
        return ResponseEntity.ok(updatedSession);
    }

    // Delete Set
    @DeleteMapping("/{sessionId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long sessionId,
            @PathVariable Long setId,
            Principal principal) {

        sessionService.deleteSet(sessionId, setId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // Add Ad-Hoc
    @PostMapping("/{sessionId}/exercises/ad-hoc")
    public ResponseEntity<SessionDTO> addAdHocExercise(
            @PathVariable Long sessionId,
            @RequestParam Long exerciseId,
            Principal principal) {

        SessionDTO updatedSession = sessionService.addAdHocExercise(sessionId, exerciseId, principal.getName());
        return ResponseEntity.ok(updatedSession);
    }

    // Delete Exercise from Session
    @DeleteMapping("/{sessionId}/exercises/{sessionExerciseId}")
    public ResponseEntity<Void> deleteExerciseFromSession(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            Principal principal) {

        sessionService.deleteSessionExercise(sessionId, sessionExerciseId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}