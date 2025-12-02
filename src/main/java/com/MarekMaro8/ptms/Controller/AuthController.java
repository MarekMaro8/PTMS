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

@RestController
@RequestMapping("/api/auth") // Bazowa ścieżka URL dla operacji uwierzytelniania
public class AuthController {
    private final ClientService clientService;
    private final TrainerService trainerService;

    public AuthController(ClientService clientService, TrainerService trainerService) {
        this.trainerService = trainerService;
        this.clientService = clientService;
    }

    @PostMapping("/client/register") //
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

    @PostMapping("/client/login")
    public ResponseEntity<Client> loginClient(@RequestBody LoginRequest loginRequest) {
        try {
            Client client = clientService.loginClient(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(client);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/trainer/register")
    public ResponseEntity<Trainer> createTrainer(@RequestBody Trainer trainer) {
        try {
            Trainer savedTrainer = trainerService.saveTrainer(trainer);
            return new ResponseEntity<>(savedTrainer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/trainer/login")
    public ResponseEntity<Trainer> loginTrainer(@RequestBody LoginRequest loginRequest) {
        try {
            Trainer trainer = trainerService.loginTrainer(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(trainer);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}