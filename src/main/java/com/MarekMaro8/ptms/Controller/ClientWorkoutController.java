package com.MarekMaro8.ptms.Controller;

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
}