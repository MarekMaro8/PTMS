package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Wstrzyknięcie Zależności (Dependency Injection)
    // Spring sam dostarczy gotową implementację ClientRepository
    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 2. Logika Biznesowa: Zapis nowego klienta
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

    // 3. Logika Biznesowa: Pobieranie wszystkich klientów
    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    // 4. Logika Biznesowa: Pobieranie klienta po ID
    public Optional<Client> findClientById(Long id) {
        // findById zwraca Optional, aby bezpiecznie obsłużyć brak wyniku
        return clientRepository.findById(id);
    }

    // 5. Logika Biznesowa: Aktualizacja klienta
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client with ID " + id + " not found."));

        // Zaktualizuj pola
        client.setFirstName(clientDetails.getFirstName());
        client.setLastName(clientDetails.getLastName());

        // Zapisz zaktualizowany obiekt (Hibernate wykona UPDATE SQL)
        return clientRepository.save(client);
    }

    // 6. Logika Biznesowa: Usuwanie klienta
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
