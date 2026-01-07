package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.session.*;
import com.MarekMaro8.ptms.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @PostMapping("/start/{workoutDayId}")
    public ResponseEntity<SessionDTO> startWorkout(
            @PathVariable Long workoutDayId,
            @Valid  @RequestBody SessionStartDTO startDto,
            Principal principal) {

        SessionDTO newSession = sessionService.startSession(principal.getName(), workoutDayId, startDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
    }

    // Finish
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<SessionDTO> finishWorkout(
            @PathVariable Long sessionId,
            Principal principal) {

        SessionDTO completedSession = sessionService.completeSession(sessionId, principal.getName());
        return ResponseEntity.ok(completedSession);
    }

    // Update Notes
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @PatchMapping("/{sessionId}/notes")
    public ResponseEntity<Void> updateNotes(
            @PathVariable Long sessionId,
            @Valid @RequestBody SessionNotesDTO notesDto,
            Principal principal) {
        sessionService.updateSessionNotes(sessionId, principal.getName(), notesDto.notes());
        return ResponseEntity.ok().build();
    }

    // Add Set
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @PostMapping("/{sessionId}/exercises/{sessionExerciseId}/sets")
    public ResponseEntity<SessionDTO> addSet(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @Valid  @RequestBody SessionSetDTO setDto,
            Principal principal) {

        SessionDTO updatedSession = sessionService.addSetToExercise(sessionId, sessionExerciseId, setDto, principal.getName());
        return ResponseEntity.ok(updatedSession);
    }

    // Delete Set
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @DeleteMapping("/{sessionId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long sessionId,
            @PathVariable Long setId,
            Principal principal) {

        sessionService.deleteSet(sessionId, setId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // Add Ad-Hoc
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @PostMapping("/{sessionId}/exercises/ad-hoc")
    public ResponseEntity<SessionDTO> addAdHocExercise(
            @PathVariable Long sessionId,
            @Valid  @RequestParam Long exerciseId,
            Principal principal) {

        SessionDTO updatedSession = sessionService.addAdHocExercise(sessionId, exerciseId, principal.getName());
        return ResponseEntity.ok(updatedSession);
    }

    // Delete Exercise from Session
    @PreAuthorize("hasAnyRole('CLIENT', 'TRAINER')")
    @DeleteMapping("/{sessionId}/exercises/{sessionExerciseId}")
    public ResponseEntity<Void> deleteExerciseFromSession(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            Principal principal) {

        sessionService.deleteSessionExercise(sessionId, sessionExerciseId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}