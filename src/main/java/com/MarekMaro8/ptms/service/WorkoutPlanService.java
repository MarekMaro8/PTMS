package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.SessionRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository;
    private final WorkoutPlanMapper workoutPlanMapper;
    private final SessionRepository sessionRepository;

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              ClientRepository clientRepository,
                              TrainerRepository trainerRepository,
                              WorkoutPlanMapper workoutPlanMapper, SessionRepository sessionRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.workoutPlanMapper = workoutPlanMapper;
        this.sessionRepository = sessionRepository;
    }

    // =================================================================================
    // CZĘŚĆ 1: METODY DLA TRENERA (Zarządzanie planami klientów)
    // =================================================================================

    // 1. Tworzenie planu
    @Transactional
    public WorkoutPlanDTO createNewWorkoutPlan(String trainerEmail, Long clientId, WorkoutPlanCreationDTO creationDto) {
        Client client = validateTrainerAccess(trainerEmail, clientId);
        WorkoutPlan newWorkoutPlan = workoutPlanMapper.toEntity(creationDto);

        if (isWorkoutPlanReady(creationDto)) {
            deactivateCurrentWorkoutPlan(clientId);
            newWorkoutPlan.setIsActive(true);
        }

        client.addWorkoutPlan(newWorkoutPlan);
        WorkoutPlan savedPlan = workoutPlanRepository.save(newWorkoutPlan);
        return workoutPlanMapper.toDto(savedPlan);
    }

    // 2. Aktywacja planu
    @Transactional
    public WorkoutPlanDTO activatePlan(String trainerEmail, Long planId) {
        WorkoutPlan planToActivate = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));

        validateTrainerAccess(trainerEmail, planToActivate.getClient().getId());

        deactivateCurrentWorkoutPlan(planToActivate.getClient().getId());
        planToActivate.setIsActive(true);

        return workoutPlanMapper.toDto(workoutPlanRepository.save(planToActivate));
    }

    // 3. [ODCZYT] Wszystkie plany konkretnego klienta
    @Transactional(readOnly = true)
    public List<WorkoutPlanDTO> getAllPlansForClient(String trainerEmail, Long clientId) {
        validateTrainerAccess(trainerEmail, clientId);

        return workoutPlanRepository.findAllByClientIdWithDays(clientId).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // 4. [ODCZYT] Aktywny plan konkretnego klienta (NOWA METODA)
    @Transactional(readOnly = true)
    public WorkoutPlanDTO getClientActivePlan(String trainerEmail, Long clientId) {
        validateTrainerAccess(trainerEmail, clientId);

        WorkoutPlan activePlan = workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Active plan not found ", "client id", clientId));

        return workoutPlanMapper.toDto(activePlan);
    }

    // 5. Usuwanie planu
    @Transactional
    public void deletePlan(String trainerEmail, Long planId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));

        validateTrainerAccess(trainerEmail, plan.getClient().getId());

        // 3. ZABEZPIECZENIE: Sprawdzamy sesje jednym szybkim zapytaniem
        if (sessionRepository.existsByWorkoutDay_WorkoutPlan_Id(planId)) {
            throw new BusinessRuleException("You cannot delete a workout plan that has associated sessions.");
        }

        // 4. Jeśli nie ma sesji, usuwamy plan (Cascade usunie też Dni i Ćwiczenia wewnątrz)
        workoutPlanRepository.delete(plan);
    }

    // =================================================================================
    // CZĘŚĆ 2: METODY DLA KLIENTA (Mój profil)
    // =================================================================================

    // 1. [ODCZYT] Mój aktywny plan
    @Transactional(readOnly = true)
    public WorkoutPlanDTO getMyActivePlan(String clientEmail) {
        Client client = getClientByEmail(clientEmail);

        WorkoutPlan plan = workoutPlanRepository.findByClientIdAndIsActiveTrue(client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active plan not found ", "client id", client.getId()));

        return workoutPlanMapper.toDto(plan);
    }

    // 2. [ODCZYT] Moja historia planów (Wszystkie moje plany)
    @Transactional(readOnly = true)
    public List<WorkoutPlanDTO> getMyAllPlans(String clientEmail) {
        Client client = getClientByEmail(clientEmail);

        return workoutPlanRepository.findAllByClientIdWithDays(client.getId()).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // Opcjonalnie: Mój konkretny plan po ID (jeśli klient chce wejść w szczegóły historii)
    @Transactional(readOnly = true)
    public WorkoutPlanDTO getMyPlanById(String clientEmail, Long planId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        if (!plan.getClient().getEmail().equals(clientEmail)) {
            throw new AccessDeniedException("You do not have access to this plan.");
        }
        return workoutPlanMapper.toDto(plan);
    }


    // =================================================================================
    // METODY POMOCNICZE (PRIVATE)
    // =================================================================================

    private Client validateTrainerAccess(String trainerEmail, Long clientId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (client.getTrainer() == null || !client.getTrainer().equals(trainer)) {
            throw new AccessDeniedException("You do not have access to this client's data.");
        }
        return client;
    }

    private Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));
    }

    private boolean isWorkoutPlanReady(WorkoutPlanCreationDTO creationDto) {
        if (creationDto.workoutDays() == null || creationDto.workoutDays().isEmpty()) return false;
        return creationDto.workoutDays().stream()
                .anyMatch(day -> day.exercises() != null && !day.exercises().isEmpty());
    }

    private void deactivateCurrentWorkoutPlan(Long clientId) {
        workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId).ifPresent(plan -> {
            plan.setIsActive(false);
            workoutPlanRepository.save(plan);
        });
    }
}