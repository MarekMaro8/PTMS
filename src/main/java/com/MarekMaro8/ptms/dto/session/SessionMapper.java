package com.MarekMaro8.ptms.dto.session;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Session;
import com.MarekMaro8.ptms.model.WorkoutDay;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionDTO toDto(Session session) {
        if (session == null) {
            return null;
        }

        // 1. Bezpieczne pobranie metadanych Klienta
        Client client = session.getClient();
        Long clientId = (client != null) ? client.getId() : null;
        String clientFullName = (client != null) ? client.getFirstName() + " " + client.getLastName() : "Brak Klienta";

        // 2. Bezpieczne pobranie metadanych Dnia Treningowego
        WorkoutDay workoutDay = session.getWorkoutDay();
        Long dayId = (workoutDay != null) ? workoutDay.getId() : null;
        String dayName = (workoutDay != null) ? workoutDay.getDayName() : "Brak Dnia";
        String focus = (workoutDay != null) ? workoutDay.getFocus() : "Brak";


        return new SessionDTO(
                session.getId(),
                session.getStartTime(),
                session.getEndTime(),
                session.isCompleted(),
                session.getNotes(),
                clientId,
                clientFullName,
                dayId,
                dayName,
                focus
        );
    }

}
