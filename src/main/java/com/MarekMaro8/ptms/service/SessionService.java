package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.session.*;
import com.MarekMaro8.ptms.model.*;
import com.MarekMaro8.ptms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final ClientRepository clientRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final SessionSetRepository sessionSetRepository;
    private final ExerciseRepository exerciseRepository; // Potrzebne do dodawania nowych ćwiczeń
    private final SessionMapper sessionMapper;

    public SessionService(SessionRepository sessionRepository,
                          WorkoutDayRepository workoutDayRepository,
                          ClientRepository clientRepository,
                          SessionExerciseRepository sessionExerciseRepository,
                          SessionSetRepository sessionSetRepository,
                          ExerciseRepository exerciseRepository,
                          SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.clientRepository = clientRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.sessionSetRepository = sessionSetRepository;
        this.exerciseRepository = exerciseRepository;
        this.sessionMapper = sessionMapper;
    }

    // ... (Metody startSession i finishSession zostają bez zmian) ...

    @Transactional
    public SessionDTO startSession(String clientEmail, Long workoutDayId) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout day not found"));

        if (!workoutDay.getWorkoutPlan().getClient().getId().equals(client.getId())) {
            throw new SecurityException("You cannot start a workout from a plan that is not yours.");
        }

        Session session = new Session();
        session.setClient(client);
        session.setWorkoutDay(workoutDay);
        session.setStartTime(LocalDateTime.now());
        session.setCompleted(false);

        Session savedSession = sessionRepository.save(session);

        if (workoutDay.getPlanExercises() != null) {
            for (PlanExercise planEx : workoutDay.getPlanExercises()) {
                SessionExercise sessionEx = new SessionExercise();
                sessionEx.setSession(savedSession);
                sessionEx.setExercise(planEx.getExercise());
                sessionExerciseRepository.save(sessionEx);
            }
        }
        return sessionMapper.toDto(savedSession);
    }

    @Transactional
    public SessionDTO finishSession(String clientEmail, Long sessionId) {
        Session session = validateSessionOwnership(clientEmail, sessionId);
        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    // --- NOWE: DODAWANIE ĆWICZENIA SPOZA PLANU (AD-HOC) ---
    @Transactional
    public void addExerciseToSession(String clientEmail, Long sessionId, AddSessionExerciseDTO dto) {
        // 1. Sprawdź czy to Twoja sesja
        Session session = validateSessionOwnership(clientEmail, sessionId);

        // 2. Znajdź ćwiczenie w słowniku
        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        // 3. Dodaj je do sesji
        SessionExercise sessionExercise = new SessionExercise();
        sessionExercise.setSession(session);
        sessionExercise.setExercise(exercise);

        // Nie ma powiązania z planem (PlanExercise), bo to "nadprogramowe"
        sessionExerciseRepository.save(sessionExercise);
    }

    // --- NOWE: USUWANIE ĆWICZENIA Z SESJI ---
    @Transactional
    public void removeExerciseFromSession(String clientEmail, Long sessionId, Long sessionExerciseId) {
        validateSessionOwnership(clientEmail, sessionId);

        SessionExercise sessionExercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Session exercise not found"));

        // Upewnij się, że to ćwiczenie faktycznie jest z tej sesji
        if (!sessionExercise.getSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("Exercise does not belong to this session");
        }

        sessionExerciseRepository.delete(sessionExercise);
    }

    // --- NOWE: AKTUALIZACJA NOTATEK SESJI ---
    @Transactional
    public void updateSessionNotes(String clientEmail, Long sessionId, String notes) {
        Session session = validateSessionOwnership(clientEmail, sessionId);

        session.setNotes(notes);
        sessionRepository.save(session);
    }

    // ... (Metody addSetToSession i deleteSetFromSession zostają bez zmian) ...
    @Transactional
    public void addSetToSession(String clientEmail, Long sessionId, Long sessionExerciseId, AddSessionSetDTO setDto) {
        validateSessionOwnership(clientEmail, sessionId);
        SessionExercise sessionExercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        if (!sessionExercise.getSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("Exercise mismatch");
        }
        SessionSet set = new SessionSet();
        set.setSessionExercise(sessionExercise);
        set.setReps(setDto.getReps());
        set.setWeight(setDto.getWeight());
        sessionSetRepository.save(set);
    }

    @Transactional
    public void deleteSetFromSession(String clientEmail, Long sessionId, Long setId) {
        validateSessionOwnership(clientEmail, sessionId);
        SessionSet set = sessionSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set not found"));
        sessionSetRepository.delete(set);
    }

    private Session validateSessionOwnership(String clientEmail, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (!session.getClient().getEmail().equals(clientEmail)) {
            throw new SecurityException("Access denied: This is not your session.");
        }
        return session;
    }
}