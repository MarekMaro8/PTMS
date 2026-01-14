package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.session.SessionDTO;
import com.MarekMaro8.ptms.dto.session.SessionMapper;
import com.MarekMaro8.ptms.dto.session.SessionSetDTO;
import com.MarekMaro8.ptms.dto.session.SessionStartDTO;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceAlreadyExistsException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
import com.MarekMaro8.ptms.model.*;
import com.MarekMaro8.ptms.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public SessionDTO startSession(String userEmail, Long workoutDayId, SessionStartDTO requestDto) {

        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new ResourceNotFoundException("Workoutday", "id", workoutDayId));

        if (workoutDay.getWorkoutPlan() == null || !workoutDay.getWorkoutPlan().getIsActive()) {
            throw new BusinessRuleException("Cannot start session for inactive or non-existing workout plan.");
        }

        Client targetClient = workoutDay.getWorkoutPlan().getClient();

        boolean isClientOwner = targetClient.getEmail().equals(userEmail);
        boolean isTrainerOwner = targetClient.getTrainer() != null && targetClient.getTrainer().getEmail().equals(userEmail);

        if (!isClientOwner && !isTrainerOwner) {
            throw new AccessDeniedException("You are not authorized to start this session.");
        }

        Session newSession = new Session();
        newSession.setClient(targetClient);
        newSession.setWorkoutDay(workoutDay);
        newSession.setStartTime(LocalDateTime.now());

        newSession.setNotes(requestDto.notes());
        newSession.setEnergyLevel(requestDto.energyLevel());
        newSession.setSleepQuality(requestDto.sleepQuality());
        newSession.setStressLevel(requestDto.stressLevel());
        newSession.setBodyWeight(requestDto.bodyWeight());

        if (workoutDay.getPlanExercises() != null) {
            int order = 1;
            for (PlanExercise planEx : workoutDay.getPlanExercises()) {
                SessionExercise sessionEx = new SessionExercise();
                sessionEx.setSession(newSession);
                sessionEx.setExercise(planEx.getExercise());
                sessionEx.setOrderIndex(order++);

                if (planEx.getSets() != null && planEx.getRepsRange() != null) {
                    sessionEx.setNotes("Cel: " + planEx.getSets() + "x" + planEx.getRepsRange());
                } else {
                    sessionEx.setNotes("Brak określonego celu.");
                }
                newSession.addSessionExercise(sessionEx);
            }
        }

        return sessionMapper.toDto(sessionRepository.save(newSession));
    }

    // POBRANIE AKTYWNEJ SESJI
    @Transactional(readOnly = true)
    public SessionDTO getActiveSession(String userEmail) {
        Client client = clientRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", userEmail));

        return sessionRepository.findByClientIdAndCompletedFalse(client.getId())
                .map(sessionMapper::toDto)
                .orElse(null);
    }

    // HISTORIA SESJI
    @Transactional(readOnly = true)
    public List<SessionDTO> getSessionHistory(String userEmail) {
        // 1. Znajdujemy klienta po mailu (tak jak lubisz)
        Client client = clientRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", userEmail));

        // 2. Pobieramy listę z repozytorium (używając metody, którą już masz)
        List<Session> sessions = sessionRepository.findAllByClientIdOrderByStartTimeDesc(client.getId());

        // 3. Mapujemy (zamieniamy) listę encji na listę DTO
        // Używamy strumieni (stream), co jest bardzo eleganckim podejściem w Javie
        return sessions.stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    // AKTYWNA SESJA KLIENTA DLA TRENERA - PO ID KLIENTA
    @Transactional(readOnly = true)
    public SessionDTO getClientActiveSessionForTrainer(String trainerEmail, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (client.getTrainer() == null || !client.getTrainer().getEmail().equals(trainerEmail)) {
            throw new AccessDeniedException("You do not have access to this client's data.");
        }
        return sessionRepository.findByClientIdAndCompletedFalse(clientId)
                .map(sessionMapper::toDto)
                .orElse(null);
    }


    // HISTORIA SESJI DLA TRENERA - PO ID KLIENTA
    @Transactional(readOnly = true)
    public List<SessionDTO> getClientHistoryForTrainer(Long clientId) {
        return sessionRepository.findAllByClientIdOrderByStartTimeDesc(clientId)
                .stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }


    // FINALIZACJA
    @Transactional
    public SessionDTO completeSession(Long sessionId, String clientEmail) {
        Session session = validateSessionOwnership(sessionId, clientEmail);

        if (session.isCompleted())
            throw new ResourceAlreadyExistsException("Session with id '" + sessionId + "' is already completed.");

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
            throw new AccessDeniedException("Exercise does not belong to this session");
        }

        SessionSet newSet = new SessionSet();
        newSet.setReps(setDto.reps());
        newSet.setWeight(setDto.weight());
        newSet.setRpe(setDto.rpe());

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
            throw new AccessDeniedException("Exercise does not belong to this session");
        }
        sessionExerciseRepository.delete(exercise);
    }

    // --- SECURITY Helper ---
    private Session validateSessionOwnership(Long sessionId, String userEmail) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", sessionId));

        String clientEmail = session.getClient().getEmail();
        String trainerEmail = session.getClient().getTrainer() != null ?
                session.getClient().getTrainer().getEmail() : null;

        if (userEmail.equals(clientEmail) || userEmail.equals(trainerEmail)) {
            return session;
        }
        throw new AccessDeniedException("Access denied to this session.");
    }
}