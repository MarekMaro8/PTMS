package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {

    // Pobierz wszystkie ćwiczenia dla konkretnej sesji (np. ID sesji = 10)
    // To się przyda, jeśli nie będziesz chciał ładować całej sesji, a tylko listę co było robione.
    List<SessionExercise> findAllBySessionId(Long sessionId);

}