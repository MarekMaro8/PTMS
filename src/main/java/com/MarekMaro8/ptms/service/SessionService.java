package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.session.SessionDTO;
import com.MarekMaro8.ptms.dto.session.SessionMapper;
import com.MarekMaro8.ptms.dto.session.SessionStartDTO;
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
    private final SessionMapper sessionMapper;

    public SessionService(SessionRepository sessionRepository, ClientRepository clientRepository, WorkoutDayRepository workoutDayRepository, SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.clientRepository = clientRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.sessionMapper = sessionMapper;
    }

    @Transactional
    // 2. Zmieniamy typ zwracany na SessionDTO
    public SessionDTO startSession(Long clientId, Long workoutDayId, SessionStartDTO requestDto) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));
        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Day template not found."));

        WorkoutPlan plan = workoutDay.getWorkoutPlan();
        if (plan == null || !plan.getIsActive()) {
            throw new IllegalStateException("Cannot start session. The workout day belongs to an inactive plan.");
        }

        // --- Logika (bez zmian) ---
        Session newSession = new Session();
        newSession.setStartTime(LocalDateTime.now());
        newSession.setNotes(requestDto.getNotes());

        client.addSession(newSession);
        workoutDay.addSession(newSession);

        // 3. ZAPIS I MAPOWANIE NA WYJŚCIE
        Session savedSession = sessionRepository.save(newSession);

        // Tutaj używamy metody, o którą pytałeś!
        return sessionMapper.toDto(savedSession);
    }

    public SessionDTO completeSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.isCompleted()) {
            throw new IllegalStateException("Session is already completed.");
        }

        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);

        Session savedSession = sessionRepository.save(session);

        // I tutaj też zwracamy bezpieczne DTO
        return sessionMapper.toDto(savedSession);
    }
}