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
    private final ClientRepository clientRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final SessionSetRepository sessionSetRepository;
    private final ExerciseRepository exerciseRepository;
    private final SessionMapper sessionMapper;

    // Wstrzykujemy wszystko, czego potrzebuje "Szef" (Sesja), żeby zarządzać "Pracownikami"
    public SessionService(SessionRepository sessionRepository,
                          ClientRepository clientRepository,
                          WorkoutDayRepository workoutDayRepository,
                          SessionExerciseRepository sessionExerciseRepository,
                          SessionSetRepository sessionSetRepository,
                          ExerciseRepository exerciseRepository,
                          SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.clientRepository = clientRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.sessionSetRepository = sessionSetRepository;
        this.exerciseRepository = exerciseRepository;
        this.sessionMapper = sessionMapper;
    }

    // ==========================================
    // 1. START SESJI (Kopiowanie z Planu)
    // ==========================================
    @Transactional
    public SessionDTO startSession(Long clientId, Long workoutDayId, SessionStartDTO requestDto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));
        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new IllegalArgumentException("Workout Day template not found."));

        if (workoutDay.getWorkoutPlan() == null || !workoutDay.getWorkoutPlan().getIsActive()) {
            throw new IllegalStateException("Cannot start session. The workout day belongs to an inactive plan.");
        }

        Session newSession = new Session();
        newSession.setClient(client);
        newSession.setWorkoutDay(workoutDay);
        newSession.setStartTime(LocalDateTime.now());
        newSession.setNotes(requestDto.getNotes());
        newSession.setEnergyLevel(requestDto.getEnergyLevel());
        newSession.setSleepQuality(requestDto.getSleepQuality());
        newSession.setStressLevel(requestDto.getStressLevel());
        newSession.setBodyWeight(requestDto.getBodyWeight());

        // TUTAJ: Kopiujemy ćwiczenia z szablonu (Planu) do Historii (Sesji)
        if (workoutDay.getPlanExercises() != null) {
            int order = 1;
            for (PlanExercise planEx : workoutDay.getPlanExercises()) {
                SessionExercise sessionEx = new SessionExercise();
                sessionEx.setSession(newSession);
                sessionEx.setExercise(planEx.getExercise()); // Przepisujemy ID ze słownika
                sessionEx.setOrderIndex(order++);
                sessionEx.setNotes("Cel z planu: " + planEx.getSets() + " serii x " + planEx.getRepsRange());

                newSession.addSessionExercise(sessionEx);
            }
        }

        Session savedSession = sessionRepository.save(newSession);
        return sessionMapper.toDto(savedSession);
    }

    // ==========================================
    // 2. ZARZĄDZANIE SERIAMI (Logika z SessionSetService)
    // ==========================================
    @Transactional
    public SessionDTO addSetToExercise(Long sessionId, Long sessionExerciseId, AddSessionSetDTO setDto) {
        // Pobieramy ćwiczenie w sesji
        SessionExercise sessionExercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Session Exercise not found."));

        // Security: Czy to ćwiczenie na pewno należy do tej sesji?
        if (!sessionExercise.getSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("Exercise does not belong to the provided session ID.");
        }

        // Automatyczne numerowanie serii (np. jest 0, to nowa ma nr 1)
        int nextSetNumber = sessionExercise.getSets().size() + 1;

        SessionSet newSet = new SessionSet();
        newSet.setSetNumber(nextSetNumber);
        newSet.setReps(setDto.getReps());
        newSet.setWeight(setDto.getWeight());
        newSet.setRpe(setDto.getRpe());

        // Korzystamy z metody pomocniczej w encji (ustawia relację dwukierunkową)
        sessionExercise.addSet(newSet);

        sessionSetRepository.save(newSet);

        // Zwracamy całą sesję, żeby frontend mógł odświeżyć widok
        return sessionMapper.toDto(sessionExercise.getSession());
    }

    // Usuwanie serii (korekta błędu)
    @Transactional
    public void deleteSet(Long sessionId, Long setId) {
        SessionSet set = sessionSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set not found"));

        // Opcjonalnie: sprawdź czy set należy do sesji sessionId dla bezpieczeństwa

        sessionSetRepository.delete(set);
    }


    // ==========================================
    // 3. ZARZĄDZANIE ĆWICZENIAMI (Logika z SessionExerciseService)
    // ==========================================

    // Dodawanie ćwiczenia "ad-hoc" (spoza planu, np. klient chce dorzucić Biceps)
    @Transactional
    public SessionDTO addAdHocExercise(Long sessionId, Long exerciseId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found in dictionary."));

        int nextOrder = session.getSessionExercises().size() + 1;

        SessionExercise newEx = new SessionExercise();
        newEx.setSession(session);
        newEx.setExercise(exercise);
        newEx.setOrderIndex(nextOrder);
        newEx.setNotes("Dodatkowe ćwiczenie");

        session.addSessionExercise(newEx);
        sessionExerciseRepository.save(newEx);

        return sessionMapper.toDto(session);
    }
    // Usuwanie ćwiczenia z sesji
    @Transactional
    public void deleteSessionExercise(Long sessionId, Long sessionExerciseId) {
        SessionExercise exercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        // Security check: Czy usuwamy ćwiczenie z właściwej sesji?
        if (!exercise.getSession().getId().equals(sessionId)) {
            throw new SecurityException("Exercise does not belong to this session");
        }

        sessionExerciseRepository.delete(exercise);
    }

    // ==========================================
    // 4. FINALIZACJA I NOTATKI
    // ==========================================
    @Transactional
    public SessionDTO completeSession(Long sessionId, Long clientId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.isCompleted()) {
            throw new IllegalStateException("Session is already completed.");
        }

        // Security check
        if (!session.getClient().getId().equals(clientId)) {
            throw new SecurityException("Session does not belong to this client");
        }

        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);

        return sessionMapper.toDto(sessionRepository.save(session));
    }

    @Transactional
    public void updateSessionNotes(Long sessionId, Long clientId, String newNotes) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getClient().getId().equals(clientId)) {
            throw new SecurityException("Unauthorized");
        }

        session.setNotes(newNotes);
        sessionRepository.save(session);
    }
}