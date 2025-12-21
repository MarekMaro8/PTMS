package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.LoginRequest;
import com.MarekMaro8.ptms.dto.client.ClientRegistrationDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerRegistrationDTO;
import com.MarekMaro8.ptms.model.Trainer;
import com.MarekMaro8.ptms.service.ClientService;
import com.MarekMaro8.ptms.service.TrainerService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientRegistrationDTO client) {
        try {
            ClientDTO savedClient = clientService.registerClient(client);
            // Zwraca klienta i status 201 Created
            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // W przypadku, gdy e-mail już istnieje, zwracamy 409 Conflict
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/client/login")
    public ResponseEntity<ClientDTO> loginClient(@RequestBody LoginRequest loginRequest) {
        try {
            ClientDTO client = clientService.loginClient(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(client);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/trainer/register")
    public ResponseEntity<TrainerDTO> createTrainer(@RequestBody TrainerRegistrationDTO trainerRegistrationDTO) {
        try {
            TrainerDTO savedTrainer = trainerService.registerTrainer(trainerRegistrationDTO);
            return new ResponseEntity<>(savedTrainer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/trainer/login")
    public ResponseEntity<TrainerDTO> loginTrainer(@RequestBody LoginRequest loginRequest) {
        try {
            TrainerDTO trainer = trainerService.loginTrainer(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(trainer);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}