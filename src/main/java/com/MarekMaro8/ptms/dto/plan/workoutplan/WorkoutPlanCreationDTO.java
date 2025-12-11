package com.MarekMaro8.ptms.dto.plan.workoutplan;

import com.MarekMaro8.ptms.dto.plan.workoutday.WorkoutDayCreationDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanCreationDTO {

    @NotBlank(message = "Nazwa planu jest wymagana")
    private String name;

    private String description;

    private List<WorkoutDayCreationDTO> workoutDays = new ArrayList<>();

    public WorkoutPlanCreationDTO() {
    }

    public WorkoutPlanCreationDTO(String name, String description, List<WorkoutDayCreationDTO> workoutDays) {
        this.name = name;
        this.description = description;
        this.workoutDays = workoutDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WorkoutDayCreationDTO> getWorkoutDays() {
        return workoutDays;
    }

    public void setWorkoutDays(List<WorkoutDayCreationDTO> workoutDays) {
        this.workoutDays = workoutDays;
    }
}

