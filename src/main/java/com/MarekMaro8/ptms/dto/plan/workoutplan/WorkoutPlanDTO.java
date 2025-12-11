package com.MarekMaro8.ptms.dto.plan.workoutplan;

import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayDTO;

import java.util.List;

public class WorkoutPlanDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isActive;
    private Long clientId;
    private List<WorkoutDayDTO> workoutDays;

    public WorkoutPlanDTO(Long id, String name, String description, boolean isActive, Long clientId, List<WorkoutDayDTO> workoutDays) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.clientId = clientId;
        this.workoutDays = workoutDays;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public Long getClientId() {
        return clientId;
    }

    public List<WorkoutDayDTO> getWorkoutDays() {
        return workoutDays;
    }
}