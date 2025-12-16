package com.MarekMaro8.ptms.dto.session;


//to co frontend wysyla :)
public class AddSessionSetDTO {
    private Integer reps;
    private Double weight;
    private Double rpe;

    // Gettery i Settery...
    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getRpe() {
        return rpe;
    }

    public void setRpe(Double rpe) {
        this.rpe = rpe;
    }
}