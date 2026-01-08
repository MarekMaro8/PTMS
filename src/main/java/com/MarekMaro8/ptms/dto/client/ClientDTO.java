package com.MarekMaro8.ptms.dto.client;

import com.MarekMaro8.ptms.model.Client;

// DTO - Prosta klasa do przesyłania danych.
// Nie ma @Entity, nie ma logiki, nie ma haseł.
public record ClientDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        Client.HealthStatus healthStatus,
        String trainerName,
        String trainerEmail,
        Long trainerId,
        String trainerNotes
) {
}

