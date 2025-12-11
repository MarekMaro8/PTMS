package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.client.ClientMapper;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerMapper;
import com.MarekMaro8.ptms.dto.trainer.TrainerRegistrationDTO;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final TrainerMapper trainerMapper;
    private final ClientMapper clientMapper;

    @Autowired
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

    @Transactional
    public TrainerDTO registerTrainer(TrainerRegistrationDTO trainerRegistrationDTO) {

        if (trainerRepository.findByEmail(trainerRegistrationDTO.getEmail()).isPresent() || clientRepository.findByEmail(trainerRegistrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Trainer with email " + trainerRegistrationDTO.getEmail() + " already exists.");
        }
        if (trainerRegistrationDTO.getPassword() == null || trainerRegistrationDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        Trainer trainerEntity = trainerMapper.toEntity(trainerRegistrationDTO);
        String hashedPassword = passwordEncoder.encode(trainerEntity.getPassword());
        trainerEntity.setPassword(hashedPassword);

        Trainer savedTrainer = trainerRepository.save(trainerEntity);

        return trainerMapper.toDto(savedTrainer);
    }

    public List<Trainer> findTrainersByClientId(Long clientId) {
        return trainerRepository.findByClients_Id(clientId);
    }

    public TrainerDTO loginTrainer(String email, String password) {
        Trainer trainer = trainerRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, trainer.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return trainerMapper.toDto(trainer);
    }


    @Transactional
    public ClientDTO assignClient(Long trainerId, Long clientId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found."));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));

        if (client.getTrainer() != null && !client.getTrainer().equals(trainer)) {
            throw new IllegalStateException("Client is already assigned to another trainer.");
        }

        // Logika biznesowa
        trainer.addClient(client);
        Client savedClient = clientRepository.save(client);

        // 4. MAPOWANIE NA KONIEC (Entity -> DTO)
        return clientMapper.toDto(savedClient);
    }

    @Transactional
    public Client unassignClient(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found."));
        client.setTrainer(null);
        return clientRepository.save(client);
    }
}

