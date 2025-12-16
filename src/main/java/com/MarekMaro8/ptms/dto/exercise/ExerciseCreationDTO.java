package com.MarekMaro8.ptms.dto.exercise;

import jakarta.validation.constraints.NotBlank;

public class ExerciseCreationDTO {

    @NotBlank(message = "Exercise name is required")
    private String name;

    @NotBlank(message = "Muscle group is required")
    private String muscleGroup;

    // Konstruktory
    public ExerciseCreationDTO() {}

    public ExerciseCreationDTO(String name, String muscleGroup) {
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    // Gettery i Settery
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }
}