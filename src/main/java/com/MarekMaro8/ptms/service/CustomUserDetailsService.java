package com.MarekMaro8.ptms.service;


import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.repository.ClientRepository;
import com.MarekMaro8.ptms.repository.TrainerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository;

    public CustomUserDetailsService(ClientRepository clientRepository, TrainerRepository trainerRepository) {
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Najpierw sprawdzamy, czy to Trener
        Optional<Trainer> trainer = trainerRepository.findByEmail(email);
        if (trainer.isPresent()) {
            return User.builder()
                    .username(trainer.get().getEmail())
                    .password(trainer.get().getPassword()) // Hasło musi być zakodowane w bazie!
                    .roles("TRAINER") // Spring automatycznie doda prefiks "ROLE_", więc będzie "ROLE_TRAINER"
                    .build();
        }

        // 2. Jeśli nie Trener, to sprawdzamy czy to Klient
        Optional<Client> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            return User.builder()
                    .username(client.get().getEmail())
                    .password(client.get().getPassword())
                    .roles("CLIENT") // Będzie "ROLE_CLIENT"
                    .build();
        }

        // 3. Jak nigdzie nie ma -> Błąd
        throw new UsernameNotFoundException("Nie znaleziono użytkownika o emailu: " + email);
    }
}
