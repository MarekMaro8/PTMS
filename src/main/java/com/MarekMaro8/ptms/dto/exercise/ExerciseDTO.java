package com.MarekMaro8.ptms.dto.exercise;

public class ExerciseDTO {
    private Long id;
    private String name;
    private String muscleGroup;

    public ExerciseDTO(Long id, String name, String muscleGroup) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    // Gettery
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getMuscleGroup() { return muscleGroup; }
}