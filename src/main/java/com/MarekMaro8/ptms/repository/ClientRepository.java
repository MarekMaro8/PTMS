package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ClientRepository extends  JpaRepository<Client, Long> { // Long - typ klucza głównego (ID) w encji Client

    Optional<Client> findByEmail(String email); // Metoda do wyszukiwania klienta po adresie e-mail
    //Optional - obiekt który może zawierać wartość lub być pusty (null)


    //metoda ktora znajdzie wszystkich uzytkownikow o tym samym nazwisku
    List<Client> findByLastName(String lastName); // Metoda do wyszukiwania klientów po nazwisku
    //List - zwraca listę klientów, którzy mają podane nazwisko






}
