package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "session_exercises")
public class SessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACJA DO SESJI (Rodzic)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    // RELACJA DO SŁOWNIKA (Co to za ćwiczenie?)
    @ManyToOne(fetch = FetchType.EAGER) // Eager, bo zazwyczaj chcemy od razu znać nazwę
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    // RELACJA DO SERII (Dzieci - "mięso")
    // CascadeType.ALL oznacza, że jak usuniesz ćwiczenie z sesji, usuną się też jego serie
    @OneToMany(mappedBy = "sessionExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionSet> sets = new ArrayList<>();

    private String notes; // Notatka do ćwiczenia w tym dniu (np. "bolało kolano")

    @Column(name = "order_index")
    private Integer orderIndex; // Kolejność w treningu (1, 2, 3...)

    // Konstruktory
    public SessionExercise() {}

    public SessionExercise(Session session, Exercise exercise, Integer orderIndex) {
        this.session = session;
        this.exercise = exercise;
        this.orderIndex = orderIndex;
    }

    // Metoda pomocnicza do dodawania serii (Twoja wygoda w kodzie)
    public void addSet(SessionSet set) {
        sets.add(set);
        set.setSessionExercise(this);
    }

    // Gettery i Settery
    public Long getId() { return id; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public List<SessionSet> getSets() { return sets; }
    public void setSets(List<SessionSet> sets) { this.sets = sets; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}