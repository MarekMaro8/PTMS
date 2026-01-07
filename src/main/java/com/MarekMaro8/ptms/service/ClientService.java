package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.client.ClientMapper;
import com.MarekMaro8.ptms.dto.client.ClientRegistrationDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerMapper;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceAlreadyExistsException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Session;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository; // Potrzebne do walidacji emaila przy rejestracji
    private final PasswordEncoder passwordEncoder;
    private final ClientMapper clientMapper;
    private final TrainerMapper trainerMapper; // NOWOŚĆ: Potrzebne, by klient mógł pobrać dane trenera jako DTO

    public ClientService(ClientRepository clientRepository,
                         TrainerRepository trainerRepository,
                         PasswordEncoder passwordEncoder,
                         ClientMapper clientMapper,
                         TrainerMapper trainerMapper) {
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientMapper = clientMapper;
        this.trainerMapper = trainerMapper;
    }


    // 1. Pobierz MÓJ profil (bezpieczne - po emailu z tokena)
    public ClientDTO getMyProfile(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));
        return clientMapper.toDto(client);
    }

    // 2. Pobierz profil MOJEGO trenera
    public TrainerDTO getMyTrainer(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() ->  new ResourceNotFoundException("Client", "email", clientEmail));

        if (client.getTrainer() == null) {
            return null; // Klient nie ma trenera
        }
        return trainerMapper.toDto(client.getTrainer());
    }

    @Transactional
    public ClientDTO registerClient(ClientRegistrationDTO clientRegistrationDTO) {
        // Sprawdzamy czy email wolny (w obu tabelach)
        if (clientRepository.findByEmail(clientRegistrationDTO.email()).isPresent() ||
                trainerRepository.findByEmail(clientRegistrationDTO.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Client with email  '" + clientRegistrationDTO.email() + "' already exists.");
        }
        // Mapowanie (Formularz -> Encja)
        Client clientEntity = clientMapper.toEntity(clientRegistrationDTO);

        // Haszowanie hasła
        String hashedPassword = passwordEncoder.encode(clientEntity.getPassword());
        clientEntity.setPassword(hashedPassword);

        // Zapis do bazy
        Client savedClient = clientRepository.save(clientEntity);
        return clientMapper.toDto(savedClient);
    }

    public ClientDTO loginClient(String email, String password) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() ->  new BusinessRuleException("Invalid email or password"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new BusinessRuleException("Invalid email or password");
        }
        return clientMapper.toDto(client);
    }


    // NOTATKI
    @Transactional
    public void updateClientNotesByTrainer(String trainerEmail, Long clientId, String newNotes) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (client.getTrainer() == null || !client.getTrainer().getId().equals(trainer.getId())) {
            throw new BusinessRuleException("Trainer is not assigned to this client.");
        }

        client.setNotes(newNotes);
        clientRepository.save(client);
    }

    @Transactional
    public void updateMyHealthStatus(String clientEmail, Client.HealthStatus status, String trainerEmail) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", clientEmail));

        if (client.getTrainer() == null || !client.getTrainer().getId().equals(trainer.getId())) {
            throw new BusinessRuleException("Trainer is not assigned to this client.");
        }

        client.setHealthStatus(status);
        clientRepository.save(client);
    }


    // =========================================================
    // METODY POMOCNICZE / ADMINISTRACYJNE
    // =========================================================


    public List<ClientDTO> findAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ClientDTO> findAllClientsWithoutTrainer() {
        return clientRepository.findAllByTrainerIsNull().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }
}