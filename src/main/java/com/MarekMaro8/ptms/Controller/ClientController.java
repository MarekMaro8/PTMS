package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // 1. Zalogowany klient pobiera SWÓJ profil
    // GET /api/clients/me
    @GetMapping("/me")
    public ResponseEntity<ClientDTO> getMyProfile(Principal principal) {
        // principal.getName() zwraca email z tokena logowania
        return ResponseEntity.ok(clientService.getMyProfile(principal.getName()));
    }

    // 2. Zalogowany klient pobiera dane SWOJEGO trenera
    // GET /api/clients/me/trainer
    @GetMapping("/me/trainer")
    public ResponseEntity<TrainerDTO> getMyTrainer(Principal principal) {
        TrainerDTO trainer = clientService.getMyTrainer(principal.getName());
        if (trainer == null) {
            return ResponseEntity.noContent().build(); // 204 No Content (brak trenera)
        }
        return ResponseEntity.ok(trainer);
    }

    // Opcjonalnie: Lista wszystkich klientów (dla admina/wyszukiwarki)
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.findAllClients());
    }

    // Opcjonalnie: Lista klientów bez trenera (dla trenerów szukających podopiecznych)
    @GetMapping("/without-trainer")
    public ResponseEntity<List<ClientDTO>> getClientsWithoutTrainer() {
        return ResponseEntity.ok(clientService.findAllClientsWithoutTrainer());
    }
}