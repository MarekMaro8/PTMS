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

    // ==================================================================================
    // METODY POMOCNICZE - SERCE LOGIKI UPRAWNIEŃ
    // ==================================================================================


    private Session validateSessionAccess(Long sessionId, String requestUserEmail) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", sessionId));

        Client sessionOwner = session.getClient();

        // 1. Czy to właściciel sesji (Klient)?
        boolean isOwner = sessionOwner.getEmail().equals(requestUserEmail);

        // 2. Czy to trener tego klienta?
        boolean isTrainer = sessionOwner.getTrainer() != null &&
                sessionOwner.getTrainer().getEmail().equals(requestUserEmail);

        if (!isOwner && !isTrainer) {
            throw new AccessDeniedException("You are not authorized to access this session.");
        }

        return session;
    }

    /**
     * Tłumaczenie: Ta metoda służy do operacji ODCZYTU (GET), gdzie nie mamy ID sesji,
     * a chcemy np. "aktywną sesję".
     * - Jeśli dzwoni Klient ->clientId jest nullem -> szukamy po mailu.
     * - Jeśli dzwoni Trener -> clientId jest wymagane -> sprawdzamy czy to jego klient.
     */
    private Client resolveClient(String userEmail, Long specificClientId) {
        // SCENARIUSZ A: Trener chce zobaczyć dane klienta (podał ID)
        if (specificClientId != null) {
            Client client = clientRepository.findById(specificClientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", specificClientId));

            // Weryfikacja: Czy ten trener opiekuje się tym klientem?
            boolean isAssignedTrainer = client.getTrainer() != null &&
                    client.getTrainer().getEmail().equals(userEmail);

            if (!isAssignedTrainer) {
                throw new AccessDeniedException("You are not authorized to access this client's data.");
            }
            return client;
        }

        // SCENARIUSZ B: Użytkownik chce swoje dane (nie podał ID, więc zakładamy, że to Klient)
        return clientRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", userEmail));
    }


    // ==================================================================================
    // LOGIKA BIZNESOWA
    // ==================================================================================

    // START SESJI
    @Transactional
    public SessionDTO startSession(String userEmail, Long workoutDayId, SessionStartDTO requestDto) {
        WorkoutDay workoutDay = workoutDayRepository.findById(workoutDayId)
                .orElseThrow(() -> new ResourceNotFoundException("Workoutday", "id", workoutDayId));

        if (workoutDay.getWorkoutPlan() == null || !workoutDay.getWorkoutPlan().getIsActive()) {
            throw new BusinessRuleException("Cannot start session for inactive or non-existing workout plan.");
        }

        Client targetClient = workoutDay.getWorkoutPlan().getClient();

        // Walidacja: Czy ten kto startuje sesję, ma do tego prawo?
        boolean isClientOwner = targetClient.getEmail().equals(userEmail);
        boolean isTrainerOwner = targetClient.getTrainer() != null && targetClient.getTrainer().getEmail().equals(userEmail);

        if (!isClientOwner && !isTrainerOwner) {
            throw new AccessDeniedException("You are not authorized to start this session.");
        }

        Session newSession = new Session();
        newSession.setClient(targetClient);
        newSession.setWorkoutDay(workoutDay);
        newSession.setStartTime(LocalDateTime.now());

        // Przepisanie danych z requestu
        newSession.setNotes(requestDto.notes());
        newSession.setEnergyLevel(requestDto.energyLevel());
        newSession.setSleepQuality(requestDto.sleepQuality());
        newSession.setStressLevel(requestDto.stressLevel());
        newSession.setBodyWeight(requestDto.bodyWeight());

        // Kopiowanie ćwiczeń z planu do sesji (Logbook)
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

    // POBRANIE AKTYWNEJ SESJI (ZMIANA: dodano parametr clientId dla trenera)
    @Transactional(readOnly = true)
    public SessionDTO getActiveSession(String userEmail, Long clientId) {
        // Używamy naszej inteligentnej metody resolveClient
        Client client = resolveClient(userEmail, clientId);

        return sessionRepository.findByClientIdAndCompletedFalse(client.getId())
                .map(sessionMapper::toDto)
                .orElse(null);
    }

    // HISTORIA SESJI (ZMIANA: dodano parametr clientId dla trenera)
    @Transactional(readOnly = true)
    public List<SessionDTO> getSessionHistory(String userEmail, Long clientId) {
        Client client = resolveClient(userEmail, clientId);

        return sessionRepository.findAllByClientIdOrderByStartTimeDesc(client.getId())
                .stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    // FINALIZACJA
    @Transactional
    public SessionDTO completeSession(Long sessionId, String userEmail) {
        // Walidujemy dostęp (niezależnie czy to Klient czy Trener)
        Session session = validateSessionAccess(sessionId, userEmail);

        if (session.isCompleted())
            throw new ResourceAlreadyExistsException("Session with id '" + sessionId + "' is already completed.");

        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    @Transactional

    public void deleteSession(Long sessionId, String userEmail) {
        Session session = validateSessionAccess(sessionId, userEmail);
        if(!session.isCompleted()) {
            throw new BusinessRuleException("Cannot delete an active session. Please complete it first.");
        }
        sessionRepository.delete(session);
    }

    // NOTATKI
    @Transactional
    public void updateSessionNotes(Long sessionId, String userEmail, String newNotes) {
        Session session = validateSessionAccess(sessionId, userEmail);
        session.setNotes(newNotes);
        sessionRepository.save(session);
    }

    // SERIE
    @Transactional
    public SessionDTO addSetToExercise(Long sessionId, Long sessionExerciseId, SessionSetDTO setDto, String userEmail) {
        // Najpierw sprawdzamy, czy user ma dostęp do sesji
        validateSessionAccess(sessionId, userEmail);

        SessionExercise sessionExercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Session exercise", "id", sessionExerciseId));

        // Spójność danych: czy to ćwiczenie na pewno należy do tej sesji?
        if (!sessionExercise.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException("Exercise does not belong to this session");
        }

        SessionSet newSet = new SessionSet();
        newSet.setReps(setDto.reps());
        newSet.setWeight(setDto.weight());
        newSet.setRpe(setDto.rpe());
        // setNumber warto by wyliczyć automatycznie (np. max + 1), ale na razie bierzemy z DTO lub null
        newSet.setSetNumber(setDto.setNumber() != null ? setDto.setNumber() : 0);

        sessionExercise.addSet(newSet);
        sessionSetRepository.save(newSet);

        return sessionMapper.toDto(sessionExercise.getSession());
    }

    @Transactional
    public void deleteSet(Long sessionId, Long setId, String userEmail) {
        validateSessionAccess(sessionId, userEmail);

        SessionSet set = sessionSetRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));

        // Dodatkowe sprawdzenie bezpieczeństwa (czy set należy do sesji)
        if (!set.getSessionExercise().getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException("Set does not belong to this session.");
        }

        sessionSetRepository.delete(set);
    }

    // AD-HOC EXERCISE (ZMIANA: używa validateSessionAccess)
    @Transactional
    public SessionDTO addAdHocExercise(Long sessionId, Long exerciseId, String userEmail) {
        Session session = validateSessionAccess(sessionId, userEmail);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId));

        SessionExercise newEx = new SessionExercise();
        newEx.setSession(session);
        newEx.setExercise(exercise);
        newEx.setNotes("Dodatkowe ćwiczenie");
        // Logika order index (na koniec listy)
        int maxOrder = session.getSessionExercises().stream()
                .mapToInt(ex -> ex.getOrderIndex() != null ? ex.getOrderIndex() : 0)
                .max().orElse(0);
        newEx.setOrderIndex(maxOrder + 1);

        session.addSessionExercise(newEx);
        sessionExerciseRepository.save(newEx);
        return sessionMapper.toDto(session);
    }

    @Transactional
    public void deleteSessionExercise(Long sessionId, Long sessionExerciseId, String userEmail) {
        validateSessionAccess(sessionId, userEmail);

        SessionExercise exercise = sessionExerciseRepository.findById(sessionExerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Session exercise", "id", sessionExerciseId));

        if (!exercise.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException("Exercise does not belong to this session");
        }
        sessionExerciseRepository.delete(exercise);
    }
}