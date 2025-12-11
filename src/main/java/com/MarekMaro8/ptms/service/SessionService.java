package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Session;
import com.MarekMaro8.ptms.model.WorkoutDay;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.SessionRepository;
import com.MarekMaro8.ptms.repository.WorkoutDayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ClientRepository clientRepository;
    private final WorkoutDayRepository workoutDayRepository;

    public SessionService(SessionRepository sessionRepository, ClientRepository clientRepository, WorkoutDayRepository workoutDayRepository) {
        this.sessionRepository = sessionRepository;
        this.clientRepository = clientRepository;
        this.workoutDayRepository = workoutDayRepository;
    }

    @Transactional
    public Session startSession(Long clientId, Long workoutDayId, Session sessionData) {
        // 1. POBRANIE: Upewnij się, że Klient i Szablon Dnia istnieją
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));
        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Day template not found."));


        if (!workoutDay.getWorkoutPlan().getIsActive() || workoutDay.getWorkoutPlan() == null) {
            throw new IllegalStateException("Cannot start session. The workout day belongs to an inactive or non-existent plan. Please assign an active plan first.");
        }


        // 2. LOGIKA: Ustawienie startowych danych sesji
        Session newSession = new Session();
        newSession.setStartTime(LocalDateTime.now());
        newSession.setNotes(sessionData.getNotes());

        // 3. SYNCHRONIZACJA: Użyj Helper Methods, by synchronizować relacje 1:N
        client.addSession(newSession);       // Klient ma nową sesję w historii
        workoutDay.addSession(newSession);   // Szablon dnia ma nową sesję w historii

        // 4. ZAPIS: Sesja jest właścicielem kluczy obcych FK
        return sessionRepository.save(newSession);
    }
    
    @Transactional
    public Session completeSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.isCompleted()) {
            throw new IllegalStateException("Session is already completed.");
        }

        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);

        return sessionRepository.save(session);
    }
}