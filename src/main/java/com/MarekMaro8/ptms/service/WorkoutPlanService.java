package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.SessionRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
                              WorkoutPlanMapper workoutPlanMapper,
                              SessionRepository sessionRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.workoutPlanMapper = workoutPlanMapper;
        this.sessionRepository = sessionRepository;
    }

    // =================================================================================
    // POBIERANIE LIST (Tu potrzebujemy clientId jeśli pyta Trener)
    // =================================================================================

    @Transactional(readOnly = true)
    public WorkoutPlanDTO getActivePlan(String userEmail, Long clientId) {
        Client client = resolveClient(userEmail, clientId);
        return workoutPlanRepository.findByClientIdAndIsActiveTrue(client.getId())
                .map(workoutPlanMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlanDTO> getAllPlans(String userEmail, Long clientId) {
        Client client = resolveClient(userEmail, clientId);
        return workoutPlanRepository.findAllByClientIdWithDays(client.getId()).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // 1. Tworzenie planu
    @Transactional
    public WorkoutPlanDTO createWorkoutPlan(String trainerEmail, Long clientId, WorkoutPlanCreationDTO creationDto) {
        // Tu potrzebujemy ID, bo plan jeszcze nie istnieje
        if (clientId == null) throw new BusinessRuleException("ID Klienta jest wymagane.");

        Client client = resolveClient(trainerEmail, clientId); // Sprawdzamy czy to Twój klient

        WorkoutPlan newWorkoutPlan = workoutPlanMapper.toEntity(creationDto);
        newWorkoutPlan.setClient(client);
        newWorkoutPlan.setIsActive(isWorkoutPlanReady(creationDto));

        if (newWorkoutPlan.getIsActive()) {
            deactivateCurrentWorkoutPlan(client.getId());
        }

        return workoutPlanMapper.toDto(workoutPlanRepository.save(newWorkoutPlan));
    }

    // =================================================================================
    // OPERACJE NA KONKRETNYM ID (Tu clientId jest zbędne - wyciągamy z planu)
    // =================================================================================

    @Transactional(readOnly = true)
    public WorkoutPlanDTO getPlanById(Long planId, String userEmail) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));

        // Sprawdzamy czy userEmail to właściciel LUB trener właściciela
        validateReadAccess(plan, userEmail);

        return workoutPlanMapper.toDto(plan);
    }

    @Transactional
    public WorkoutPlanDTO activatePlan(Long planId, String trainerEmail) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));

        // Sprawdzamy czy trainerEmail to trener właściciela planu
        validateModificationAccess(plan, trainerEmail);

        deactivateCurrentWorkoutPlan(plan.getClient().getId());
        plan.setIsActive(true);

        return workoutPlanMapper.toDto(workoutPlanRepository.save(plan));
    }

    @Transactional
    public void deletePlan(Long planId, String trainerEmail) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));

        validateModificationAccess(plan, trainerEmail);

        if (sessionRepository.existsByWorkoutDay_WorkoutPlan_Id(planId)) {
            throw new BusinessRuleException("Nie można usunąć planu, który ma historię sesji.");
        }

        workoutPlanRepository.delete(plan);
    }

    // =================================================================================
    // PRIVATE HELPERS
    // =================================================================================

    private Client resolveClient(String userEmail, Long specificClientId) {
        if (specificClientId != null) {
            Client client = clientRepository.findById(specificClientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", specificClientId));

            // SECURITY: Czy ten trener może oglądać tego klienta?
            if (client.getTrainer() == null || !client.getTrainer().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("Brak dostępu do danych tego klienta.");
            }
            return client;
        }
        return clientRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", userEmail));
    }

    private void validateReadAccess(WorkoutPlan plan, String userEmail) {
        Client owner = plan.getClient();
        boolean isOwner = owner.getEmail().equals(userEmail);
        boolean isAssignedTrainer = owner.getTrainer() != null && owner.getTrainer().getEmail().equals(userEmail);

        if (!isOwner && !isAssignedTrainer) {
            throw new AccessDeniedException("Brak dostępu do tego planu.");
        }
    }

    private void validateModificationAccess(WorkoutPlan plan, String trainerEmail) {
        Client owner = plan.getClient();
        boolean isAssignedTrainer = owner.getTrainer() != null && owner.getTrainer().getEmail().equals(trainerEmail);

        if (!isAssignedTrainer) {
            throw new AccessDeniedException("Tylko przypisany trener może modyfikować ten plan.");
        }
    }

    private Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));
    }

    private boolean isWorkoutPlanReady(WorkoutPlanCreationDTO creationDto) {
        return creationDto.workoutDays() != null && !creationDto.workoutDays().isEmpty() &&
                creationDto.workoutDays().stream().anyMatch(d -> d.exercises() != null && !d.exercises().isEmpty());
    }

    private void deactivateCurrentWorkoutPlan(Long clientId) {
        workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId).ifPresent(p -> {
            p.setIsActive(false);
            workoutPlanRepository.save(p);
        });
    }
}