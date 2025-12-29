package com.MarekMaro8.ptms.dto.plan.planexercise;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlanExerciseCreationDTO {
    @NotBlank(message = "Exercise name is required")
    private Long exerciseId; // ZMIANA: ID zamiast Nazwy

    @NotNull
    private Integer sets;
    private String repsRange;

    private Integer rpe;


    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public String getRepsRange() {
        return repsRange;
    }

    public void setRepsRange(String repsRange) {
        this.repsRange = repsRange;
    }

    public Integer getRpe() {
        return rpe;
    }

    public void setRpe(Integer rpe) {
        this.rpe = rpe;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }
}