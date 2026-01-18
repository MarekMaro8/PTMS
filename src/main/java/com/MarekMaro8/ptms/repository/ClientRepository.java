package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// repository - warstwa dostępu do danych (DAO) dla encji Client /  dao - Data Access Object
public interface ClientRepository extends  JpaRepository<Client, Long> { // Long - typ klucza głównego (ID) w encji Client

    Optional<Client> findByEmail(String email); // Metoda do wyszukiwania klienta po adresie e-mail

    List<Client> findAllByTrainerIsNull(); // Metoda do wyszukiwania klientów bez przypisanego trenera

    long count();

//tutaj sa te 3 metody bo inne klasy nie wiedza jak obslugiwac SQL, dlatego musza brac z tego interfejsu :)
    // JpaRepository dostarcza podstawowe metody CRUD (Create, Read, Update, Delete)
    // takie jak save, findById, findAll, deleteById itp.
    // Nie musimy ich implementować sami.

}
