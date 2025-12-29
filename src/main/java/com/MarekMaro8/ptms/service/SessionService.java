package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.session.*;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceAlreadyExistsException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
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

    // START SESJI
    @Transactional
    public SessionDTO startSession(String clientEmail, Long workoutDayId, SessionStartDTO requestDto) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", clientEmail));
        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new ResourceNotFoundException("Workoutday", "id", workoutDayId));

        if (workoutDay.getWorkoutPlan() == null || !workoutDay.getWorkoutPlan().getIsActive()) {
            throw new BusinessRuleException("Cannot start session for inactive or non-existing workout plan.");
        }
        // Opcjonalnie: sprawdź czy plan należy do tego klienta
        if (!workoutDay.getWorkoutPlan().getClient().equals(client)) {
            throw new BusinessRuleException("Workout day does not belong to this client.");
        }

        Session newSession = new Session();
        newSession.setClient(client);
        newSession.setWorkoutDay(workoutDay);
        newSession.setStartTime(LocalDateTime.now());
        newSession.setNotes(requestDto.getNotes());
        // ... setEnergy, Sleep, Stress itd.

        // Kopiowanie ćwiczeń
        if (workoutDay.getPlanExercises() != null) {
            int order = 1;
            for (PlanExercise planEx : workoutDay.getPlanExercises()) {
                SessionExercise sessionEx = new SessionExercise();
                sessionEx.setSession(newSession);
                sessionEx.setExercise(planEx.getExercise());
                sessionEx.setOrderIndex(order++);
                sessionEx.setNotes("Cel: " + planEx.getSets() + "x" + planEx.getRepsRange());
                newSession.addSessionExercise(sessionEx);
            }
        }

        return sessionMapper.toDto(sessionRepository.save(newSession));
    }

    // FINALIZACJA
    @Transactional
    public SessionDTO completeSession(Long sessionId, String clientEmail) {
        Session session = validateSessionOwnership(sessionId, clientEmail);

        if (session.isCompleted()) throw new ResourceAlreadyExistsException("Session with id '" + sessionId + "' is already completed.");

        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    // NOTATKI
    @Transactional
    public void updateSessionNotes(Long sessionId, String clientEmail, String newNotes) {
        Session session = validateSessionOwnership(sessionId, clientEmail);
        session.setNotes(newNotes);
        sessionRepository.save(session);
    }

    // SERIE
    @Transactional
    public SessionDTO addSetToExercise(Long sessionId, Long sessionExerciseId, SessionSetDTO setDto, String clientEmail) { // <--- TU ZMIANA
        validateSessionOwnership(sessionId, clientEmail);

        SessionExercise sessionExercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Session exercise", "id", sessionExerciseId));

        if (!sessionExercise.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException("Exercise does not belong to this session");
        }

        SessionSet newSet = new SessionSet();
        newSet.setReps(setDto.getReps());   // Bierzemy z SessionSetDTO
        newSet.setWeight(setDto.getWeight()); // Bierzemy z SessionSetDTO
        newSet.setRpe(setDto.getRpe());       // Bierzemy z SessionSetDTO

        sessionExercise.addSet(newSet);
        sessionSetRepository.save(newSet);

        return sessionMapper.toDto(sessionExercise.getSession());
    }

    @Transactional
    public void deleteSet(Long sessionId, Long setId, String clientEmail) {
        validateSessionOwnership(sessionId, clientEmail);
        SessionSet set = sessionSetRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));
        sessionSetRepository.delete(set);
    }

    // AD-HOC EXERCISE
    @Transactional
    public SessionDTO addAdHocExercise(Long sessionId, Long exerciseId, String clientEmail) {
        Session session = validateSessionOwnership(sessionId, clientEmail);
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId));

        SessionExercise newEx = new SessionExercise();
        newEx.setSession(session);
        newEx.setExercise(exercise);
        newEx.setNotes("Dodatkowe ćwiczenie");

        session.addSessionExercise(newEx);
        sessionExerciseRepository.save(newEx);
        return sessionMapper.toDto(session);
    }

    @Transactional
    public void deleteSessionExercise(Long sessionId, Long sessionExerciseId, String clientEmail) {
        validateSessionOwnership(sessionId, clientEmail);
        SessionExercise exercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Session exercise", "id", sessionExerciseId));

        if (!exercise.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException("Exercise does not belong to this session");
        }
        sessionExerciseRepository.delete(exercise);
    }

    // --- SECURITY Helper ---
    private Session validateSessionOwnership(Long sessionId, String userEmail) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->new ResourceNotFoundException("Session", "id", sessionId));

        String clientEmail = session.getClient().getEmail();
        String trainerEmail = session.getClient().getTrainer() != null ?
                session.getClient().getTrainer().getEmail() : null;

        // Jeśli zalogowany email to email klienta LUB email jego trenera -> dajemy dostęp
        if (userEmail.equals(clientEmail) || userEmail.equals(trainerEmail)) {
            return session;
        }
        throw new BusinessRuleException("Access denied to this session.");
    }
}