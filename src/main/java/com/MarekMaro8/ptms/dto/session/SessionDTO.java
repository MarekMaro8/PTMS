package com.MarekMaro8.ptms.dto.session;

import java.time.LocalDateTime;
import java.util.List;

public class SessionDTO {
    private final Long id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final boolean completed;
    private final String notes;

    // Metadane Klienta i Planu
    private final Long clientId;
    private final String clientFullName;
    private final Long workoutDayId;
    private final String workoutDayName;
    private final String workoutDayFocus;

    // --- NOWE POLA (Wellness) ---
    private final Integer energyLevel;
    private final Integer sleepQuality;
    private final Integer stressLevel;
    private final Double bodyWeight;

    // 2. NOWE POLE
    private final List<SessionExerciseDTO> sessionExercises;

    // 3. NOWY KONSTRUKTOR (15 argumentów)
    public SessionDTO(Long id, LocalDateTime startTime, LocalDateTime endTime, boolean completed, String notes,
                      Long clientId, String clientFullName, Long workoutDayId, String workoutDayName, String workoutDayFocus,
                      Integer energyLevel, Integer sleepQuality, Integer stressLevel, Double bodyWeight,
                      List<SessionExerciseDTO> sessionExercises) { // <--- DODANY ARGUMENT
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completed = completed;
        this.notes = notes;
        this.clientId = clientId;
        this.clientFullName = clientFullName;
        this.workoutDayId = workoutDayId;
        this.workoutDayName = workoutDayName;
        this.workoutDayFocus = workoutDayFocus;
        this.energyLevel = energyLevel;
        this.sleepQuality = sleepQuality;
        this.stressLevel = stressLevel;
        this.bodyWeight = bodyWeight;
        this.sessionExercises = sessionExercises; // <--- PRZYPISANIE
    }

    // --- GETTERY ---
    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getNotes() {
        return notes;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public Long getWorkoutDayId() {
        return workoutDayId;
    }

    public String getWorkoutDayName() {
        return workoutDayName;
    }

    public String getWorkoutDayFocus() {
        return workoutDayFocus;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public Integer getSleepQuality() {
        return sleepQuality;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public Double getBodyWeight() {
        return bodyWeight;
    }

    public List<SessionExerciseDTO> getSessionExercises() {
        return sessionExercises;
    }
}