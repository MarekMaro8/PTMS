package com.MarekMaro8.ptms.dto;

// DTO - Prosta klasa do przesyłania danych.
// Nie ma @Entity, nie ma logiki, nie ma haseł.
    public class ClientDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        // Możemy tu dodać np. imię trenera, zamiast całego obiektu Trenera!
        private String trainerName;

        // Konstruktor, który przyjmuje Encję i "przepisuje" dane
        // To eliminuje problem Proxy i Rekurencji!
        public ClientDTO(Long id, String firstName, String lastName, String email, String trainerName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.trainerName = trainerName;
        }

        // Gettery (Settery nie są konieczne, jeśli używamy konstruktora)
        public Long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getTrainerName() { return trainerName; }
    }

