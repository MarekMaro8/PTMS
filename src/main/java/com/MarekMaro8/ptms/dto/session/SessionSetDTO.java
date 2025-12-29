package com.MarekMaro8.ptms.dto.session;
//to co frontend odbiera :)
public class SessionSetDTO {
    private Long id;
    private Integer setNumber;
    private Integer reps;
    private Double weight;
    private Double rpe;

    public SessionSetDTO(Long id, Integer setNumber, Integer reps, Double weight, Double rpe) {
        this.id = id;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.rpe = rpe;
    }


    // Gettery
    public Long getId() {
        return id;
    }

    public Integer getReps() {
        return reps;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getRpe() {
        return rpe;
    }

    public Integer getSetNumber() {
        return setNumber;
    }
}
