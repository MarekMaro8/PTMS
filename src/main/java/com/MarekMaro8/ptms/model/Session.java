package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 do Dnia Treningowego (Wiele Sesji do Jednego Szablonu Dnia) - POPRAWIONE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_day_id")
    private WorkoutDay workoutDay; // Zmieniono z 'WorkoutPlan workoutDayId'

    // N:1 do Klienta (Wiele Sesji do Jednego Klienta) - POPRAWIONE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client; // Zmieniono z 'client_id'

    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // Dodano, żeby liczyć czas trwania
    private boolean completed = false; // Ustawiono wartość domyślną

    public Session() {
    }

    public Session(String notes, LocalDateTime startTime, LocalDateTime endTime, boolean completed) {
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completed = completed;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client){
        this.client = client;
        }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }


    // --- Konstruktor, Gettery i Settery ---
    // Musisz usunąć parametr 'sessionName' z konstruktora i dostosować pozostałe.
    // ...
}