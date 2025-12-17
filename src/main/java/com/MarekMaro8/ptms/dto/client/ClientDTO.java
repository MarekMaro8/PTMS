package com.MarekMaro8.ptms.dto.client;

// DTO - Prosta klasa do przesyłania danych.
// Nie ma @Entity, nie ma logiki, nie ma haseł.
public class ClientDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    // Możemy tu dodać np. imię trenera, zamiast całego obiektu Trenera!
    private final String trainerName;
    private String trainerEmail;
    private Long trainerId;

    // Konstruktor, który przyjmuje Encję i "przepisuje" dane
    // To eliminuje problem Proxy i Rekurencji!
    public ClientDTO(Long id, String firstName, String lastName, String email, String trainerName, String trainerEmail, Long trainerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.trainerName = trainerName;
        this.trainerEmail = trainerEmail;
        this.trainerId = trainerId;
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
}

