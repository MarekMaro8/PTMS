package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    // Metoda do wyszukiwania ćwiczenia po nazwie (np. żeby sprawdzić czy "Przysiad" już istnieje)
    Optional<Exercise> findByName(String name);

    // Metoda sprawdzająca istnienie (zwraca true/false) - przydatne przy walidacji
    boolean existsByName(String name);
}