package com.MarekMaro8.ptms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Exercise name cannot be empty")
    @Column(nullable = false, unique = true) // Nazwy muszą być unikalne!
    private String name;

    private String muscleGroup; // np. "Legs", "Chest" (Opcjonalne)

    private String description; // Opcjonalne pole na opis ćwiczenia

    // Konstruktory
    public Exercise() {}

    public Exercise(String name, String muscleGroup, String description) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.description = description;
    }

    // Gettery i Settery
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}