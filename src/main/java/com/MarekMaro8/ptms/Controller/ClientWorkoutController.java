package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.session.AddSessionSetDTO;
import com.MarekMaro8.ptms.dto.session.SessionDTO;
import com.MarekMaro8.ptms.dto.session.SessionNotesDTO;
import com.MarekMaro8.ptms.dto.session.SessionStartDTO;
import com.MarekMaro8.ptms.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// 1. Prefiks URL: Wszystko w kontekście konkretnego klienta
@RequestMapping("/api/clients/{clientId}/workouts")
public class ClientWorkoutController {

    private final SessionService sessionService;

    // Konstruktor (Wstrzykiwanie zależności)
    public ClientWorkoutController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    //Rozpoczecie sesji treingowej
    @PostMapping("/start/{workoutDayId}")
    public ResponseEntity<SessionDTO> startWorkout(
            @PathVariable Long clientId,
            @PathVariable Long workoutDayId,
            @RequestBody SessionStartDTO startDto) {

        // Wywołujemy serwis, który tworzy sesję, ustawia datę startu i status
        SessionDTO newSession = sessionService.startSession(clientId, workoutDayId, startDto);

        // Zwracamy status 201 (Created), bo powstał nowy zasób w bazie
        return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
    }

    // Zakończenie sesji treningowej
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<SessionDTO> finishWorkout(
            @PathVariable Long clientId,
            @PathVariable Long sessionId) {

        SessionDTO completedSession = sessionService.completeSession(sessionId, clientId);

        return ResponseEntity.ok(completedSession);
    }

    // Aktualizacja notatek do sesji treningowej PATCH - często używane do częściowych aktualizacji zasobów
    @PatchMapping("/{sessionId}/notes")
    public ResponseEntity<Void> updateNotes(
            @PathVariable Long clientId,
            @PathVariable Long sessionId,
            @RequestBody SessionNotesDTO notesDto) { // Tu używamy dedykowanego DTO

        sessionService.updateSessionNotes(sessionId, clientId, notesDto.getNotes());
        return ResponseEntity.ok().build();
    }

    // Dodaj Serię (np. "Zrobiłem 10 powtórzeń 100kg")
    @PostMapping("/{sessionId}/exercises/{sessionExerciseId}/sets")
    public ResponseEntity<SessionDTO> addSet(
            @PathVariable Long clientId,
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @RequestBody AddSessionSetDTO setDto) {

        // Wywołujemy metodę, którą dodaliśmy do SessionService
        SessionDTO updatedSession = sessionService.addSetToExercise(sessionId, sessionExerciseId, setDto);
        return ResponseEntity.ok(updatedSession);
    }

    // Usuń Serię (gdy użytkownik się pomylił)
    @DeleteMapping("/{sessionId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long clientId,
            @PathVariable Long sessionId,
            @PathVariable Long setId) {

        sessionService.deleteSet(sessionId, setId);
        return ResponseEntity.noContent().build(); // 204 No Content (Standard przy usuwaniu)
    }

    // 3. Dodaj ćwiczenie spoza planu (Ad-Hoc)
    @PostMapping("/{sessionId}/exercises/ad-hoc")
    public ResponseEntity<SessionDTO> addAdHocExercise(
            @PathVariable Long clientId,
            @PathVariable Long sessionId,
            @RequestParam Long exerciseId) { // ID ze słownika przekazujemy jako parametr ?exerciseId=...

        SessionDTO updatedSession = sessionService.addAdHocExercise(sessionId, exerciseId);
        return ResponseEntity.ok(updatedSession);
    }

    // 4. Usuń ćwiczenie z sesji (np. boli mnie bark, usuwam Wyciskanie)
    // URL: /api/clients/{id}/workouts/{sessionId}/exercises/{sessionExerciseId}
    @DeleteMapping("/{sessionId}/exercises/{sessionExerciseId}")
    public ResponseEntity<Void> deleteExerciseFromSession(
            @PathVariable Long clientId,
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId) {

        sessionService.deleteSessionExercise(sessionId, sessionExerciseId);
        return ResponseEntity.noContent().build();
    }
}