package com.MarekMaro8.ptms.dto.session;

import java.time.LocalDateTime;
import java.util.List;

public record SessionDTO (
     LocalDateTime startTime,
     LocalDateTime endTime,
     boolean completed,
     String notes,

    // Metadane Klienta i Planu
     Long clientId,
     String clientFullName,
     Long workoutDayId,
     String workoutDayName,
     String workoutDayFocus,

    // --- NOWE POLA (Wellness) ---
     Integer energyLevel,
     Integer sleepQuality,
     Integer stressLevel,
     Double bodyWeight,

     List<SessionExerciseDTO> sessionExercises,
     Long id
){}