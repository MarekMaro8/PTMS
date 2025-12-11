package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ClientRepository clientRepository;
    private final WorkoutPlanMapper workoutPlanMapper;
    // Będziemy potrzebować też serwisu do komunikacji z API i do tworzenia Dni Treningowych!

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository, ClientRepository clientRepository, WorkoutPlanMapper workoutPlanMapper) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.clientRepository = clientRepository;
        this.workoutPlanMapper = workoutPlanMapper;
    }

    private void deactivateWorkoutPlan(Long clientId) {
        Optional<WorkoutPlan> oldWorkoutPlan = workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId);
        oldWorkoutPlan.ifPresent(plan -> {
            plan.setIsActive(false);
            workoutPlanRepository.save(plan);
        });
    }


    @Transactional
    public WorkoutPlanDTO createNewWorkoutPlan(Long clientId, WorkoutPlanCreationDTO creationDto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));

        WorkoutPlan newWorkoutPlan = workoutPlanMapper.toEntity(creationDto);

        deactivateWorkoutPlan(clientId);

        client.addWorkoutPlan(newWorkoutPlan); // Ustawia relację client <-> plan
        newWorkoutPlan.setIsActive(true);      // Nowy plan jest domyślnie aktywny

        WorkoutPlan savedPlan = workoutPlanRepository.save(newWorkoutPlan);

        return workoutPlanMapper.toDto(savedPlan);
    }

    @Transactional
    public WorkoutPlanDTO getActivePlan(Long clientId) {
        WorkoutPlan plan = workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId)
                .orElseThrow(() -> new IllegalArgumentException("No active plan found for client."));

        return workoutPlanMapper.toDto(plan);
    }

    public List<WorkoutPlanDTO> getAllPlansForClient(Long clientId) {
        return workoutPlanRepository.findAllByClientId(clientId).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }
}