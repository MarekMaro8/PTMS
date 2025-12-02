package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository, PasswordEncoder passwordEncoder, ClientRepository clientRepository) {
        this.passwordEncoder = passwordEncoder;
        this.trainerRepository = trainerRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Trainer saveTrainer(Trainer trainer) {

        Optional<Trainer> existingTrainer = trainerRepository.findByEmail(trainer.getEmail());

        if (existingTrainer.isPresent()) {
            throw new IllegalArgumentException("Trainer with email " + trainer.getEmail() + " already exists.");
        }
        if (trainer.getPassword() == null || trainer.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        String hashedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(hashedPassword);

        return trainerRepository.save(trainer);
    }

    public List<Trainer> findTrainersByClientId(Long clientId) {
        return trainerRepository.findByClients_Id(clientId);
    }

    public Trainer loginTrainer(String email, String password) {
        Trainer trainer = trainerRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, trainer.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return trainer;
    }


    public Trainer updateTrainer(Long trainerId, Trainer updatedTrainer) {
        Trainer existingTrainer = trainerRepository.findById(trainerId).orElseThrow(() -> new IllegalArgumentException("Trainer not found."));
        existingTrainer.setFirstName(updatedTrainer.getFirstName());
        existingTrainer.setLastName(updatedTrainer.getLastName());
        return trainerRepository.save(existingTrainer);
    }

    @Transactional
    public Client assignClient(Long trainerId, Long clientId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new IllegalArgumentException("Trainer not found."));
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found."));

        // Sprawdzenie, czy klient nie ma już innego trenera
        if (client.getTrainer() != null && !client.getTrainer().equals(trainer)) {
            throw new IllegalStateException("Client is already assigned to another trainer.");
        }

        //Musisz wykonać akcję w Javie (synchronizacja obiektu) i akcję w bazie danych (utrwalenie transakcji) – stąd potrzeba obu linii.
        trainer.addClient(client); // to zapisuje w obiekcie w pamieci

        // Zapis klienta (bo to on jest stroną 'Many' i trzyma FK (klucz obcy))
        return clientRepository.save(client); //to zapisuje w bazie dancyh
    }

    @Transactional
    public Client unassignClient(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found."));
        client.setTrainer(null);
        return clientRepository.save(client);
    }
}

