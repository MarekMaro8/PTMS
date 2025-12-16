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

        return new SessionDTO(
                session.getId(),
                session.getStartTime(),
                session.getEndTime(),
                session.isCompleted(),
                session.getNotes(),
                (client != null) ? client.getId() : null,
                (client != null) ? client.getFirstName() + " " + client.getLastName() : "Brak",
                (wDay != null) ? wDay.getId() : null,
                (wDay != null) ? wDay.getDayName() : "Brak",
                (wDay != null) ? wDay.getFocus() : "Brak",
                session.getEnergyLevel(),
                session.getSleepQuality(),
                session.getStressLevel(),
                session.getBodyWeight(),
                exercisesDto // <-- POPRAWKA 1: Musisz dodać ten argument na końcu!
        );
    }

    // Metoda pomocnicza: SessionExercise -> SessionExerciseDTO
    private SessionExerciseDTO toSessionExerciseDto(SessionExercise ex) {
        List<SessionSetDTO> setsDto = Collections.emptyList();
        if (ex.getSets() != null) {
            setsDto = ex.getSets().stream()
                    .map(this::toSessionSetDto)
                    .collect(Collectors.toList());
        }

        Long exDictId = (ex.getExercise() != null) ? ex.getExercise().getId() : null;
        String exName = (ex.getExercise() != null) ? ex.getExercise().getName() : "Nieznane ćwiczenie";
        String muscle = (ex.getExercise() != null) ? ex.getExercise().getMuscleGroup() : "";

        return new SessionExerciseDTO(
                ex.getId(),
                exDictId,
                exName,
                muscle,
                ex.getOrderIndex(),
                ex.getNotes(),
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
                set.getRpe(),
        );
    }
}