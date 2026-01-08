package com.MarekMaro8.ptms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client {

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public enum HealthStatus {
        HEALTHY,      // Zdrowy
        SICK,         // Chory
        INJURED,      // Kontuzja
        REHABILITATION // Rehabilitacja
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacja N:1 (Wiele Klientów ma Jednego Trenera)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
//    @JsonIgnoreProperties({"clients", "hibernateLazyInitializer", "handler"})
    private Trainer trainer;

    // Relacja 1:N (Jeden Klient ma Wiele Planów)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<WorkoutPlan> workoutPlans = new HashSet<>();

    // Relacja 1:N (Jeden Klient ma Wiele Sesji Historycznych)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Session> sessions = new HashSet<>();

    private String firstName;
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING) // Zapisujemy w bazie jako tekst (np. "HEALTHY")
    private HealthStatus healthStatus = HealthStatus.HEALTHY; // Domyślnie zdrowy

    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    public Client() {
    }

    public Client(String firstName, String lastName, String email, String notes ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.notes = notes;
    }

    // Musisz użyć tej metody, gdy Trener przypisuje Klientowi nowy Plan
    public void addWorkoutPlan(WorkoutPlan plan) {
        this.workoutPlans.add(plan);
        plan.setClient(this);
    }

    // Musisz użyć tej metody, gdy Klient dodaje nową Sesję do historii
    public void addSession(Session session) {
        this.sessions.add(session);
        session.setClient(this);
    }



    public Long getId() {
        return id;
    }


    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public Set<WorkoutPlan> getWorkoutPlans() {
        return workoutPlans;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
