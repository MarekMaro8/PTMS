package com.MarekMaro8.ptms.dto.session;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Session;
import com.MarekMaro8.ptms.model.SessionExercise;
import com.MarekMaro8.ptms.model.SessionSet;
import com.MarekMaro8.ptms.model.WorkoutDay;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionMapper {

    // Główna metoda mapująca Sesję
    public SessionDTO toDto(Session session) {
        if (session == null) return null;

        // 1. Mapujemy listę ćwiczeń
        List<SessionExerciseDTO> exercisesDto = Collections.emptyList();
        if (session.getSessionExercises() != null) {
            exercisesDto = session.getSessionExercises().stream()
                    .map(this::toSessionExerciseDto)
                    .collect(Collectors.toList());
        }

        Client client = session.getClient();
        WorkoutDay wDay = session.getWorkoutDay();

        Long clientId = null;
        String clientName = "Brak";

        if(client != null){
            clientId = client.getId();
            clientName = client.getFirstName() + " " + client.getLastName();
        }
        Long workoutDayId = null;
        String workoutDayName = "Nieznany dzień treningowy";
        String workoutDayFocus = "Nieznany fokus";

        if(wDay != null){
            workoutDayId = wDay.getId();
            workoutDayName = wDay.getDayName();
            workoutDayFocus = wDay.getFocus();
        }


        return new SessionDTO(
                session.getStartTime(),
                session.getEndTime(),
                session.isCompleted(),
                session.getNotes(),
                clientId,
                clientName,
                workoutDayId,
                workoutDayName,
                workoutDayFocus,
                session.getEnergyLevel(),
                session.getSleepQuality(),
                session.getStressLevel(),
                session.getBodyWeight(),
                exercisesDto,
                session.getId()
        );
    }

    // Metoda pomocnicza: SessionExercise -> SessionExerciseDTO
    private SessionExerciseDTO toSessionExerciseDto(SessionExercise sessionExercise
    ) {
        List<SessionSetDTO> setsDto = Collections.emptyList();
        if (sessionExercise.getSets() != null) {
            setsDto = sessionExercise.getSets().stream()
                    .map(this::toSessionSetDto)
                    .collect(Collectors.toList());
        }

        Long exerciseId = null;
        String exName =  "Nieznane ćwiczenie";
        String muscle =  "";

        if(sessionExercise .getExercise() == null){
            exName = sessionExercise.getExercise().getName();
            muscle = sessionExercise.getExercise().getMuscleGroup();
            exerciseId = sessionExercise.getExercise().getId();
        }

        return new SessionExerciseDTO(
                sessionExercise.getId(),
                exerciseId,
                exName,
                muscle,
                sessionExercise.getOrderIndex(),
                sessionExercise.getNotes(),
                setsDto
        );
    }

    // Metoda pomocnicza: SessionSet -> SessionSetDTO
    private SessionSetDTO toSessionSetDto(SessionSet set) {
        return new SessionSetDTO(
                set.getId(),
                set.getSetNumber(),
                set.getReps(),
                set.getWeight(),
                set.getRpe()
        );
    }
}