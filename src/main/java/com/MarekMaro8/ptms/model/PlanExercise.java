package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_exercises")
public class PlanExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Pole przechowujące identyfikator ćwiczenia z ExerciseDB
    @Column(nullable = false)
    private String name;
    private Integer sets;
    private String repsRange;
    private Integer rpe;

    // Relacja: Wiele instrukcji należy do Jednego Dnia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_day_id")
    private WorkoutDay workoutDay;

    public PlanExercise() {}

    // KONSTRUKTOR INICJALIZUJĄCY: Kluczowe dane + Relacja
    public PlanExercise(String name, Integer sets, String repsRange, Integer rpe, WorkoutDay workoutDay) {
        this.name = name;
        this.sets = sets;
        this.repsRange = repsRange;
        this.rpe = rpe;
        this.workoutDay = workoutDay; // Ustawienie relacji (FK)
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

    public WorkoutDay getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(WorkoutDay workoutDay) {
        this.workoutDay = workoutDay;
    }
}