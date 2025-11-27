package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.LoginRequest;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.service.ClientService;
import com.MarekMaro8.ptms.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 1. Oznacza, że ta klasa obsługuje żądania HTTP
@RequestMapping("/api/auth") // 2. Definiuje bazową ścieżkę URL
public class AuthController {


    private final ClientService clientService;
    private final TrainerService trainerService;

    @Autowired
    public AuthController(ClientService clientService, TrainerService trainerService) {
        this.trainerService = trainerService;
        this.clientService = clientService;
    }

    @PostMapping("/register/client") // 3. Mapuje żądania POST do /api/clients
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        try {
            Client savedClient = clientService.saveClient(client);
            // Zwraca klienta i status 201 Created
            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // W przypadku, gdy e-mail już istnieje, zwracamy 409 Conflict
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login/client")
    public ResponseEntity<Client> loginClient(@RequestBody LoginRequest loginRequest) {
        try {
            Client client = clientService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(client);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/register/trainer") // 3. Mapuje żądania POST do /api/clients
    public ResponseEntity<Trainer> createTrainer(@Valid @RequestBody Trainer trainer) {
        try {
            Trainer savedTrainer = trainerService.saveTrainer(trainer);
            // Zwraca klienta i status 201 Created
            return new ResponseEntity<>(savedTrainer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // W przypadku, gdy e-mail już istnieje, zwracamy 409 Conflict
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login/trainer")
    public ResponseEntity<Trainer> loginTrainer(@RequestBody LoginRequest loginRequest) {
        try {
            Trainer trainer = trainerService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(trainer);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}