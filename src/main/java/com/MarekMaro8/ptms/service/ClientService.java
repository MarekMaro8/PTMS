package com.MarekMaro8.ptms.service;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.client.ClientMapper;
import com.MarekMaro8.ptms.dto.client.ClientRegistrationDTO;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//SERVICE - warstwa logiki biznesowej dla encji Client
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientMapper clientMapper;

    // 1. Wstrzyknięcie Zależności (Dependency Injection)
    // Spring sam dostarczy gotową implementację ClientRepository
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ClientDTO> getClientsDtoByTrainerId(Long trainerId) {
        return clientRepository.findAllByTrainerId(trainerId).stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ClientDTO> findAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClientDTO registerClient(ClientRegistrationDTO clientRegistrationDTO) {
        //Sprawdzamy czy email wolny
        if (clientRepository.findByEmail(clientRegistrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Client with email " + clientRegistrationDTO.getEmail() + " already exists.");
        }
        //Mapowanie (Formularz -> Encja)
        Client clientEntity = clientMapper.toEntity(clientRegistrationDTO);

        // Pobieramy surowe hasło z encji (które włożył tam mapper), szyfrujemy i nadpisujemy
        String hashedPassword = passwordEncoder.encode(clientEntity.getPassword());
        clientEntity.setPassword(hashedPassword);

        //Zapis do bazy
        Client savedClient = clientRepository.save(clientEntity);

        //Mapowanie zwrotne (Encja -> Wizytówka)
        // Zwracamy obiekt bez hasła!
        return clientMapper.toDto(savedClient);
    }



    public ClientDTO loginClient(String email, String password) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return clientMapper.toDto(client);
    }

}
