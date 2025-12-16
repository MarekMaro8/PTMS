package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.SessionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionSetRepository extends JpaRepository<SessionSet, Long> {

    // Pobierz wszystkie serie dla konkretnego ćwiczenia w sesji
    List<SessionSet> findAllBySessionExerciseId(Long sessionExerciseId);

    // Będziesz tu mógł pisać zaawansowane zapytania do wykresów, np.:
    // @Query("SELECT s FROM SessionSet s JOIN s.sessionExercise se WHERE se.exercise.name = :name AND se.session.date > :date")
}