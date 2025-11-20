package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// SERVICE - warstwa logiki biznesowej dla encji Client
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Wstrzyknięcie Zależności (Dependency Injection)
    // Spring sam dostarczy gotową implementację ClientRepository
    // Konstruktor z @Autowired - wstrzykuje zależności do klasy ClientService
    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Client saveClient(Client client) {

        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());

        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("Client with email " + client.getEmail() + " already exists.");
        }
        String hashedPassword = passwordEncoder.encode(client.getPassword());
        client.setPassword(hashedPassword);

        return clientRepository.save(client);
        // save - metoda z JpaRepository, która zapisuje obiekt w bazie danych
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> findClientById(Long id) {
        // findById zwraca Optional, aby bezpiecznie obsłużyć brak wyniku
        return clientRepository.findById(id);
    }

    public Client login(String email, String password) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return client;
    }


    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client with ID " + id + " not found."));

        // Zaktualizuj pola
        client.setFirstName(clientDetails.getFirstName());
        client.setLastName(clientDetails.getLastName());

        // Zapisz zaktualizowany obiekt (Hibernate wykona UPDATE SQL)
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
