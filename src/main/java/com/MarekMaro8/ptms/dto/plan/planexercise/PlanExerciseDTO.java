package com.MarekMaro8.ptms.dto.plan.planexercise;

public class PlanExerciseDTO {
    private Long id;
    private String name;
    private Integer sets;
    private String repsRange;
    private Integer rpe;

    public PlanExerciseDTO(Long id, String name, Integer sets, String repsRange, Integer rpe) {
        this.id = id;
        this.name = name;
        this.sets = sets;
        this.repsRange = repsRange;
        this.rpe = rpe;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getSets() {
        return sets;
    }

    public String getRepsRange() {
        return repsRange;
    }

    public Integer getRpe() {
        return rpe;
    }
}