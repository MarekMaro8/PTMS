package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "session_sets")
public class SessionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACJA DO ĆWICZENIA W SESJI
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_exercise_id", nullable = false)
    private SessionExercise sessionExercise;

    @Column(nullable = false)
    private Integer setNumber; // Numer serii: 1, 2, 3...

    @Column(nullable = false)
    private Integer reps; // Wykonane powtórzenia

    @Column(nullable = false)
    private Double weight; // Ciężar w kg (Double jest lepszy niż Integer dla ciężarów typu 12.5)

    private Double rpe; // RPE (może być null, może być 8.5)

    // Konstruktory
    public SessionSet() {}

    public SessionSet(Integer setNumber, Integer reps, Double weight, Double rpe) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.rpe = rpe;
    }

    // Gettery i Settery
    public Long getId() { return id; }

    public SessionExercise getSessionExercise() { return sessionExercise; }
    public void setSessionExercise(SessionExercise sessionExercise) { this.sessionExercise = sessionExercise; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getRpe() { return rpe; }
    public void setRpe(Double rpe) { this.rpe = rpe; }

}