package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanCreationDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanDTO;
import com.MarekMaro8.ptms.dto.plan.workoutplan.WorkoutPlanMapper;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.model.WorkoutPlan;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import com.MarekMaro8.ptms.repository.WorkoutPlanRepository;
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

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              ClientRepository clientRepository,
                              TrainerRepository trainerRepository,
                              WorkoutPlanMapper workoutPlanMapper) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.workoutPlanMapper = workoutPlanMapper;
    }

    // =====================================================================
    // METODY DLA KLIENTA (Działają na "JA" / Principal Email)
    // =====================================================================

    @Transactional(readOnly = true)
    public WorkoutPlanDTO getMyActivePlan(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        // Szukamy aktywnego planu tego konkretnego klienta
        return workoutPlanRepository.findByClientIdAndIsActiveTrue(client.getId())
                .map(workoutPlanMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("No active plan found for this client."));
    }

    @Transactional(readOnly = true)
    public List<WorkoutPlanDTO> getMyAllPlans(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        return workoutPlanRepository.findAllByClientId(client.getId()).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // METODY DLA TRENERA (Zarządzanie klientem + Security Check)
    // =====================================================================

    @Transactional(readOnly = true)
    public WorkoutPlanDTO getClientActivePlan(String trainerEmail, Long clientId) {
        validateTrainerAccess(trainerEmail, clientId); // <--- SECURITY CHECK

        return workoutPlanRepository.findByClientIdAndIsActiveTrue(clientId)
                .map(workoutPlanMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("No active plan found for client ID: " + clientId));
    }

    @Transactional
    public WorkoutPlanDTO createPlanForClient(String trainerEmail, Long clientId, WorkoutPlanCreationDTO creationDto) {
        Client client = validateTrainerAccess(trainerEmail, clientId); // <--- SECURITY CHECK i pobranie klienta

        // Walidacja logiczna (czy plan ma sens?)
        if (!isWorkoutPlanReady(creationDto)) {
            throw new IllegalArgumentException("Plan must contain at least one day with exercises.");
        }

        // Tworzenie encji
        WorkoutPlan newWorkoutPlan = workoutPlanMapper.toEntity(creationDto);

        // Logika biznesowa: Nowy plan staje się od razu aktywny?
        // Zazwyczaj tak, więc dezaktywujemy stare.
        deactivateCurrentWorkoutPlans(client.getId());
        newWorkoutPlan.setIsActive(true);

        client.addWorkoutPlan(newWorkoutPlan); // Relacja dwukierunkowa

        WorkoutPlan savedPlan = workoutPlanRepository.save(newWorkoutPlan);
        return workoutPlanMapper.toDto(savedPlan);
    }



    @Transactional
    public WorkoutPlanDTO activatePlan(String trainerEmail, Long planId) {
        WorkoutPlan planToActivate = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Workout plan not found."));

        // Sprawdzamy, czy trener opiekuje się właścicielem tego planu
        Long ownerId = planToActivate.getClient().getId();
        validateTrainerAccess(trainerEmail, ownerId); // <--- SECURITY CHECK

        deactivateCurrentWorkoutPlans(ownerId);

        planToActivate.setIsActive(true);
        workoutPlanRepository.save(planToActivate);

        return workoutPlanMapper.toDto(planToActivate);
    }

    // Lista wszystkich planów klienta (widok dla trenera)
    @Transactional(readOnly = true)
    public List<WorkoutPlanDTO> getClientPlans(String trainerEmail, Long clientId) {
        validateTrainerAccess(trainerEmail, clientId);
        return workoutPlanRepository.findAllByClientId(clientId).stream()
                .map(workoutPlanMapper::toDto)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // METODY POMOCNICZE (Private)
    // =====================================================================

    // To jest nasza metoda "Policyjna". Sprawdza, czy trener ma prawo do klienta.
    // Zwraca obiekt Client, żeby nie szukać go drugi raz w bazie.
    private Client validateTrainerAccess(String trainerEmail, Long clientId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (client.getTrainer() == null || !client.getTrainer().equals(trainer)) {
            throw new SecurityException("Access denied: You are not the trainer of this client.");
        }
        return client;
    }

    private void deactivateCurrentWorkoutPlans(Long clientId) {
        List<WorkoutPlan> activePlans = workoutPlanRepository.findAllByClientId(clientId).stream()
                .filter(WorkoutPlan::getIsActive)
                .collect(Collectors.toList());

        activePlans.forEach(plan -> plan.setIsActive(false));
        workoutPlanRepository.saveAll(activePlans);
    }

    private boolean isWorkoutPlanReady(WorkoutPlanCreationDTO creationDto) {
        if (creationDto.getWorkoutDays() == null || creationDto.getWorkoutDays().isEmpty()) {
            return false;
        }
        return creationDto.getWorkoutDays().stream()
                .anyMatch(day -> day.getExercises() != null && !day.getExercises().isEmpty());
    }
}
