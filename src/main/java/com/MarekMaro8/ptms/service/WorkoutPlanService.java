package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ClientRepository clientRepository;
    // Będziemy potrzebować też serwisu do komunikacji z API i do tworzenia Dni Treningowych!

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository, ClientRepository clientRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.clientRepository = clientRepository;
    }

    private void deactivateWorkoutPlan(Long clientId) {
        Optional<WorkoutPlan> oldWorkoutPlan = workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId);
        oldWorkoutPlan.ifPresent(plan -> {
            plan.setIsActive(false);
            workoutPlanRepository.save(plan);
            //if (oldActivePlan.isPresent()) {
            //    WorkoutPlan plan = oldActivePlan.get(); // Trzeba ręcznie wyciągnąć obiekt
            //    plan.setActive(false);
            //    workoutPlanRepository.save(plan);
            //} mozna tez tak napisac ale lambda jest lepsza i szybsza
        });
    }


    @Transactional
    public WorkoutPlan createNewWorkoutPlan(Long clientId, WorkoutPlan newWorkoutPlan) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));
        deactivateWorkoutPlan(clientId);
        client.addWorkoutPlan(newWorkoutPlan);
        newWorkoutPlan.setIsActive(true);
        return workoutPlanRepository.save(newWorkoutPlan);
    }

    @Transactional
    public WorkoutPlan getActivePlan(Long clientId) {
        return workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId)
                .orElseThrow(() -> new IllegalArgumentException("No active plan found for client."));
    }

    public List<WorkoutPlan> getAllPlansOfClient(Long clientId) {
    return workoutPlanRepository.findAllByClientId(clientId);
    }
}