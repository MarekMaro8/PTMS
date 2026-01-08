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
import com.MarekMaro8.ptms.repository.TrainerRepository; // Dodano
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository; // Dodano
    private final WorkoutPlanMapper workoutPlanMapper;

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              ClientRepository clientRepository,
                              TrainerRepository trainerRepository,
                              WorkoutPlanMapper workoutPlanMapper) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.workoutPlanMapper = workoutPlanMapper;
    }

    // --- DLA TRENERA ---

    @Transactional
    public WorkoutPlanDTO createNewWorkoutPlan(String trainerEmail, Long clientId, WorkoutPlanCreationDTO creationDto) {
        // Security Check: Czy to Twój klient?
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

    @Transactional
    public WorkoutPlanDTO activatePlan(String trainerEmail, Long planId) {
        WorkoutPlan planToActivate = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));

        // Security Check
        validateTrainerAccess(trainerEmail, planToActivate.getClient().getId());

        deactivateCurrentWorkoutPlan(planToActivate.getClient().getId());
        planToActivate.setIsActive(true);
        workoutPlanRepository.save(planToActivate);
        return workoutPlanMapper.toDto(planToActivate);
    }

    public List<WorkoutPlanDTO> getAllPlansForClient(String trainerEmail, Long clientId) {
        validateTrainerAccess(trainerEmail, clientId);
        return workoutPlanRepository.findAllByClientId(clientId).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- DLA KLIENTA (/ME) ---

    @Transactional(readOnly = true)
    public WorkoutPlanDTO getMyActivePlan(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", clientEmail));

        WorkoutPlan plan = workoutPlanRepository.findByClientIdAndIsActiveTrue(client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active workoutPlan", "client mail", clientEmail));

        return workoutPlanMapper.toDto(plan);
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlanDTO> getMyAllPlans(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", clientEmail));

        return workoutPlanRepository.findAllByClientId(client.getId()).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- POMOCNICZE ---

    private Client validateTrainerAccess(String trainerEmail, Long clientId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (client.getTrainer() == null || !client.getTrainer().equals(trainer)) {
            throw new BusinessRuleException("Client is not assigned to you.");
        }
        return client;
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