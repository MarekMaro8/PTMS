package com.MarekMaro8.ptms.dto.session;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class SessionExerciseDTO {
    private Long id;

    // Dane ze słownika (Spłaszczone dla wygody Frontendu)
    private Long exerciseId;

    @NotBlank(message = "Muscle name is required")
    private String exerciseName;

    @NotBlank(message = "Muscle group is required")
    private String muscleGroup;   // Opcjonalnie, żeby np. pokazać ikonkę "Nogi"

    // Dane z wykonania
    private Integer orderIndex;
    private String notes;

    // Lista dzieci (Serie)
    private List<SessionSetDTO> sets;

    public SessionExerciseDTO(Long id, Long exerciseId, String exerciseName, String muscleGroup, Integer orderIndex, String notes, List<SessionSetDTO> sets) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.muscleGroup = muscleGroup;
        this.orderIndex = orderIndex;
        this.notes = notes;
        this.sets = sets;
    }

    // Gettery
    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public String getMuscleGroup() { return muscleGroup; }
    public Integer getOrderIndex() { return orderIndex; }
    public String getNotes() { return notes; }
    public List<SessionSetDTO> getSets() { return sets; }
}