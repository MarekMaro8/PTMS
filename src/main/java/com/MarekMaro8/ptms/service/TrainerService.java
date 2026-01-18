package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.client.ClientMapper;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerMapper;
import com.MarekMaro8.ptms.dto.trainer.TrainerRegistrationDTO;
import com.MarekMaro8.ptms.exception.BusinessRuleException;
import com.MarekMaro8.ptms.exception.ResourceAlreadyExistsException;
import com.MarekMaro8.ptms.exception.ResourceNotFoundException;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final TrainerMapper trainerMapper;
    private final ClientMapper clientMapper;

    public TrainerService(TrainerRepository trainerRepository,
                          PasswordEncoder passwordEncoder,
                          ClientRepository clientRepository,
                          TrainerMapper trainerMapper,
                          ClientMapper clientMapper) {
        this.passwordEncoder = passwordEncoder;
        this.trainerRepository = trainerRepository;
        this.clientRepository = clientRepository;
        this.trainerMapper = trainerMapper;
        this.clientMapper = clientMapper;
    }


    // 1. Pobierz profil zalogowanego trenera
    public TrainerDTO getMyProfile(String email) {
        Trainer trainer = trainerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", email));
        return trainerMapper.toDto(trainer);
    }

    // 2. Pobierz listę TYLKO MOICH klientów
    public List<ClientDTO> getMyClients(String trainerEmail) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));

        return trainer.getClients().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    // 3. Pobierz szczegóły klienta Z WERYFIKACJĄ (Czy to mój klient?)
    public ClientDTO getMyClientDetails(String trainerEmail, Long clientId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() ->new ResourceNotFoundException("Trainer", "email", trainerEmail));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (client.getTrainer() == null || !client.getTrainer().getId().equals(trainer.getId())) {
            throw new AccessDeniedException("Client is not assigned to you.");
        }

        return clientMapper.toDto(client);
    }

    // 4. Przypisz klienta do MNIE (zalogowanego trenera)
    @Transactional
    public ClientDTO assignClientToMe(String trainerEmail, Long clientId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (client.getTrainer() != null && !client.getTrainer().equals(trainer)) {
            throw new AccessDeniedException("Client is already assigned to another trainer.");
        }

        trainer.addClient(client); // Relacja + FK
        Client savedClient = clientRepository.save(client);

        return clientMapper.toDto(savedClient);
    }

    // 5. Odepnij klienta ode MNIE (zalogowanego trenera)
    @Transactional
    public void unassignClientFromMe(String trainerEmail, Long clientId) {
        Trainer trainer = trainerRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "email", trainerEmail));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        // Security Check: Czy odpinam swojego klienta?
        if (client.getTrainer() != null && client.getTrainer().equals(trainer)) {
            client.setTrainer(null);
            clientRepository.save(client);
        } else {
            throw new AccessDeniedException("Cannot unassign a client that isn't yours.");
        }
    }

    @Transactional
    public TrainerDTO registerTrainer(TrainerRegistrationDTO dto) {
        if (trainerRepository.findByEmail(dto.email()).isPresent() ||
                clientRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Trainer with email '" + dto.email() + "' already exists.");
        }

        Trainer trainer = trainerMapper.toEntity(dto);
        String hashedPassword = passwordEncoder.encode(trainer.getPassword());
        trainer.setPassword(hashedPassword);

        Trainer savedTrainer = trainerRepository.save(trainer);
        return trainerMapper.toDto(savedTrainer);
    }

    public TrainerDTO loginTrainer(String email, String password) {
        Trainer trainer = trainerRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleException("Invalid email or password"));

        if (!passwordEncoder.matches(password, trainer.getPassword())) {
            throw new BusinessRuleException("Invalid email or password");
        }
        return trainerMapper.toDto(trainer);
    }

    public List<TrainerDTO> findAllTrainers() {
        return trainerRepository.findAll().stream()
                .map(trainerMapper::toDto)
                .collect(Collectors.toList());
    }


    // Metoda pomocnicza (np. dla widoku publicznego profilu)
    public Optional<TrainerDTO> getTrainerById(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .map(trainerMapper::toDto);
    }
}