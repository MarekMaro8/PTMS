package com.MarekMaro8.ptms.dto.plan.workoutday;

import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseDTO;

import java.util.List;

public class WorkoutDayDTO {
    private Long id;
    private String dayName;
    private String focus;
    private List<PlanExerciseDTO> exercises;

    public WorkoutDayDTO(Long id, String dayName, String focus, List<PlanExerciseDTO> exercises) {
        this.id = id;
        this.dayName = dayName;
        this.focus = focus;
        this.exercises = exercises;
    }

    public Long getId() {
        return id;
    }

    public String getDayName() {
        return dayName;
    }

    public String getFocus() {
        return focus;
    }

    public List<PlanExerciseDTO> getExercises() {
        return exercises;
    }
}