package com.MarekMaro8.ptms.dto.plan.planexercise;

public class PlanExerciseDTO {
    private Long id;
    private Long exerciseId; // ID ze słownika
    private String name;     // Nazwa ze słownika (do wyświetlania)
    private Integer sets;
    private String repsRange;
    private Integer rpe;

    public PlanExerciseDTO(Long id, Long exerciseId, String name, Integer sets, String repsRange, Integer rpe) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.name = name;
        this.sets = sets;
        this.repsRange = repsRange;
        this.rpe = rpe;
    }

    // Gettery
    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }
    public String getName() { return name; }
    public Integer getSets() { return sets; }
    public String getRepsRange() { return repsRange; }
    public Integer getRpe() { return rpe; }
}