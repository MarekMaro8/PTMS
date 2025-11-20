package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "workout_days")
public class WorkoutDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 (Wiele Dni do Jednego Planu)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id")
    private WorkoutPlan workoutPlan; // Zmieniono z 'workoutPlanId'

    private String dayName;
    private String focus;

    // 1:N (Jeden Dzień ma Wiele Instrukcji Ćwiczeń) - POPRAWIONO
    @OneToMany(mappedBy = "workoutDay", cascade = CascadeType.ALL)
    private Set<PlanExercise> planExercises = new HashSet<>();

    // 1:N (Jeden Dzień jest wykonywany Wiele Razy/Sesji) - POPRAWIONO
    @OneToMany(mappedBy = "workoutDay", cascade = CascadeType.ALL)
    private Set<Session> sessions = new HashSet<>();


    public WorkoutDay() {}
    public WorkoutDay(String dayName, String focus) {
        this.dayName = dayName;
        this.focus = focus;
    }

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

    public Long getId() {
        return id;
    }
}