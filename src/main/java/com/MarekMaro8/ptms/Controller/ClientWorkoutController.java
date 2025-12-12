package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.session.SessionDTO;
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

    /**
     * 1. START TRENINGU
     * URL: POST /api/clients/{clientId}/workouts/start/{workoutDayId}
     * * Dlaczego tak?
     * Klient wchodzi w konkretny Dzień Treningowy (np. "Dzień A - Klatka") i klika START.
     * Przekazujemy SessionStartDTO, bo tam mogą być wstępne notatki (np. "Czuję się dzisiaj słabo").
     */
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

    /**
     * 2. ZAKOŃCZENIE TRENINGU
     * URL: POST /api/clients/{clientId}/workouts/{sessionId}/finish
     * * Dlaczego POST a nie PUT?
     * Bo "zakończenie" to akcja biznesowa, a nie tylko aktualizacja pól.
     * Dodatkowo: clientId w URL jest pod przyszłe zabezpieczenia (Middleware).
     */
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<SessionDTO> finishWorkout(
            @PathVariable Long clientId,
            @PathVariable Long sessionId) {

        SessionDTO completedSession = sessionService.completeSession(sessionId, clientId);

        return ResponseEntity.ok(completedSession);
    }


    //toDo mozliwosc dopisania notatek na koniec treningu :)
}