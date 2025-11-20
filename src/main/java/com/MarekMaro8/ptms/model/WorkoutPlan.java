package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // N:1 (Wiele Planów ma Jednego Klienta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // 1:N (Jeden Plan ma Wiele Dni Treningowych) - DODANO
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL)
    private Set<WorkoutDay> workoutDays = new HashSet<>();

    private String name;
    private String description;


    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

}