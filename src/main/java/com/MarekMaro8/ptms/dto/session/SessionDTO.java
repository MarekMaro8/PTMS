package com.MarekMaro8.ptms.dto.session;

import java.time.LocalDateTime;

public class SessionDTO {
    private final Long id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final boolean completed;
    private final String notes;

    // Metadane Klienta
    private final Long clientId;
    private final String clientFullName;

    // Metadane Dnia Treningowego
    private final Long workoutDayId;
    private final String workoutDayName;
    private final String workoutDayFocus;


    public SessionDTO(Long id, LocalDateTime startTime, LocalDateTime endTime, boolean completed, String notes,
                      Long clientId, String clientFullName, Long workoutDayId, String workoutDayName, String workoutDayFocus) {
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
    }

    // Gettery
    public Long getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isCompleted() { return completed; }
    public String getNotes() { return notes; }
    public Long getClientId() { return clientId; }
    public String getClientFullName() { return clientFullName; }
    public Long getWorkoutDayId() { return workoutDayId; }
    public String getWorkoutDayName() { return workoutDayName; }
    public String getWorkoutDayFocus() { return workoutDayFocus; }
}
