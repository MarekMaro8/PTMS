package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.config.JwtService;
import com.MarekMaro8.ptms.dto.AuthResponse;
import com.MarekMaro8.ptms.dto.LoginRequest;
import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.client.ClientRegistrationDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerRegistrationDTO;
import com.MarekMaro8.ptms.service.ClientService;
import com.MarekMaro8.ptms.service.CustomUserDetailsService;
import com.MarekMaro8.ptms.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Bazowa ścieżka URL dla operacji uwierzytelniania
public class AuthController {
    private final ClientService clientService;
    private final TrainerService trainerService;
    private final JwtService jwtService; // Dodajemy JwtService
    private final CustomUserDetailsService userDetailsService; // Dodajemy UserDetailsService

    public AuthController(ClientService clientService, TrainerService trainerService, JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.clientService = clientService;
        this.trainerService = trainerService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/client/register") //
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientRegistrationDTO client) {
        ClientDTO savedClient = clientService.registerClient(client);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @PostMapping("/client/login")
    public ResponseEntity<AuthResponse> loginClient(@RequestBody LoginRequest loginRequest) {
        ClientDTO client = clientService.loginClient(loginRequest.getEmail(), loginRequest.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(client.getEmail());
        String token = jwtService.generateToken(userDetails);

        // 3. ZMIANA: Tworzymy AuthResponse przekazując ID i Imię z obiektu client
        // Dzięki temu Frontend dostanie: { "token": "...", "role": "CLIENT", "id": 1, "firstName": "Marek" }
        return ResponseEntity.ok(new AuthResponse(
                token,
                "CLIENT",
                client.getId(),
                client.getFirstName(),
                client.getLastName()
        ));
    }


    @PostMapping("/trainer/register")
    public ResponseEntity<TrainerDTO> createTrainer(@Valid @RequestBody TrainerRegistrationDTO trainerRegistrationDTO) {
        TrainerDTO savedTrainer = trainerService.registerTrainer(trainerRegistrationDTO);
        return new ResponseEntity<>(savedTrainer, HttpStatus.CREATED);
    }

    @PostMapping("/trainer/login")
    public ResponseEntity<AuthResponse> loginTrainer(@RequestBody LoginRequest loginRequest) {
        TrainerDTO trainer = trainerService.loginTrainer(loginRequest.getEmail(), loginRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(trainer.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(
                token,
                "TRAINER",
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName()
        ));
    }
}
