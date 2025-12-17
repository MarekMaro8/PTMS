package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.session.AddSessionExerciseDTO;
import com.MarekMaro8.ptms.dto.session.AddSessionSetDTO;
import com.MarekMaro8.ptms.dto.session.SessionDTO;
import com.MarekMaro8.ptms.dto.session.SessionNotesDTO;
import com.MarekMaro8.ptms.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/clients/workouts")
public class ClientWorkoutController {

    private final SessionService sessionService;

    public ClientWorkoutController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // 1. Start sesji
    @PostMapping("/start/{workoutDayId}")
    public ResponseEntity<SessionDTO> startSession(
            @PathVariable Long workoutDayId,
            Principal principal) {
        return ResponseEntity.ok(sessionService.startSession(principal.getName(), workoutDayId));
    }

    // 2. Koniec sesji
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<SessionDTO> finishSession(
            @PathVariable Long sessionId,
            Principal principal) {
        return ResponseEntity.ok(sessionService.finishSession(principal.getName(), sessionId));
    }

    // --- NOWE: AKTUALIZACJA NOTATEK ---
    // PATCH jest idealny do zmiany pojedynczego pola (notatek)
    @PatchMapping("/{sessionId}/notes")
    public ResponseEntity<Void> updateNotes(
            @PathVariable Long sessionId,
            @RequestBody SessionNotesDTO notesDto,
            Principal principal) {

        sessionService.updateSessionNotes(principal.getName(), sessionId, notesDto.getNotes());
        return ResponseEntity.ok().build();
    }

    // --- NOWE: DODAJ ĆWICZENIE DO SESJI (SPOZA PLANU) ---
    @PostMapping("/{sessionId}/exercises")
    public ResponseEntity<Void> addExerciseToSession(
            @PathVariable Long sessionId,
            @RequestBody AddSessionExerciseDTO dto,
            Principal principal) {

        sessionService.addExerciseToSession(principal.getName(), sessionId, dto);
        return ResponseEntity.ok().build();
    }

    // --- NOWE: USUŃ ĆWICZENIE Z SESJI ---
    @DeleteMapping("/{sessionId}/exercises/{sessionExerciseId}")
    public ResponseEntity<Void> deleteExerciseFromSession(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            Principal principal) {

        sessionService.removeExerciseFromSession(principal.getName(), sessionId, sessionExerciseId);
        return ResponseEntity.noContent().build();
    }

    // 3. Dodaj serię
    @PostMapping("/{sessionId}/exercises/{sessionExerciseId}/sets")
    public ResponseEntity<Void> addSet(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @RequestBody AddSessionSetDTO setDto,
            Principal principal) {

        sessionService.addSetToSession(principal.getName(), sessionId, sessionExerciseId, setDto);
        return ResponseEntity.ok().build();
    }

    // 4. Usuń serię
    @DeleteMapping("/{sessionId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long sessionId,
            @PathVariable Long setId,
            Principal principal) {

        sessionService.deleteSetFromSession(principal.getName(), sessionId, setId);
        return ResponseEntity.noContent().build();
    }
}