package com.MarekMaro8.ptms.dto.plan.workoutday;
import com.MarekMaro8.ptms.dto.plan.planexercise.PlanExerciseCreationDTO;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class WorkoutDayCreationDTO {
    @NotBlank(message = "Day name is required")
    private String dayName;
    @NotBlank(message = "Focus is required")
    private String focus;

    // Lista ćwiczeń do stworzenia w tym dniu
    private List<PlanExerciseCreationDTO> exercises;

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public List<PlanExerciseCreationDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<PlanExerciseCreationDTO> exercises) {
        this.exercises = exercises;
    }
}
