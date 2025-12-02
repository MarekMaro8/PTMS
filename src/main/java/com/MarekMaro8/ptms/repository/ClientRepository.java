package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// repository - warstwa dostępu do danych (DAO) dla encji Client /  dao - Data Access Object
public interface ClientRepository extends  JpaRepository<Client, Long> { // Long - typ klucza głównego (ID) w encji Client

    Optional<Client> findByEmail(String email); // Metoda do wyszukiwania klienta po adresie e-mail
    //Optional - obiekt który może zawierać wartość lub być pusty (null)

    List<Client> findByLastName(String lastName); // Metoda do wyszukiwania klientów po nazwisku
    //List - obiekt który może zawierać wiele wartości (klientów) lub być pusty (null)

    List<Client> findAllByTrainerId(Long trainerId); // Metoda do wyszukiwania klientów przypisanych do konkretnego trenera po jego ID>

//tutaj sa te 2 metody bo inne klasy nie wiedza jak obslugiwac SQL, dlatego musza brac z tego interfejsu :)
    // JpaRepository dostarcza podstawowe metody CRUD (Create, Read, Update, Delete)
    // takie jak save, findById, findAll, deleteById itp.
    // Nie musimy ich implementować sami.

}
