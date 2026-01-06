package com.MarekMaro8.ptms.dto.client;

import com.MarekMaro8.ptms.model.Client;

// DTO - Prosta klasa do przesyłania danych.
// Nie ma @Entity, nie ma logiki, nie ma haseł.
public class ClientDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Client.HealthStatus healthStatus;
    private final String trainerName;
    private String trainerEmail;
    private Long trainerId;
    private final String trainerNotes;

    // Konstruktor, który przyjmuje Encję i "przepisuje" dane
    // To eliminuje problem Proxy i Rekurencji!
    public ClientDTO(Long id, String firstName, String lastName, String email, Client.HealthStatus healthStatus, String trainerName, String trainerEmail, Long trainerId, String trainerNotes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.healthStatus = healthStatus;
        this.trainerName = trainerName;
        this.trainerEmail = trainerEmail;
        this.trainerId = trainerId;
        this.trainerNotes = trainerNotes;
    }

    // Gettery (Settery nie są konieczne, jeśli używamy konstruktora)
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public Client.HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public String getTrainerNotes() {
        return trainerNotes;
    }
}

