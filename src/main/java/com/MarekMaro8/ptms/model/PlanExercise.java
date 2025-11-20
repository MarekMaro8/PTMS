package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_exercises")
public class PlanExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID z API to najlepiej String, żeby obsłużyć litery
    private String externalExerciseId; // Zmieniono z Long na String

    // N:1 (Wiele Instrukcji należy do Jednego Dnia)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_day_id")
    private WorkoutDay workoutDay; // Zmieniono z 'workoutDayId'

    private String name; // Zmieniono z Long na String
    private Integer sets; // Zmieniono z Long na Integer
    private String repsRange; // Zakres powtórzeń (np. "8-12")
    private Integer rpe; // Zmieniono z Long na Integer, jeśli to skala 1-10

    public PlanExercise() {
    }

    public PlanExercise(String name, Integer sets, String repsRange, Integer rpe) {
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

    public void setName(String name) {
        this.name = name;
    }

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
}