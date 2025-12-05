package com.MarekMaro8.ptms.Controller;


import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.service.ClientService;
import com.MarekMaro8.ptms.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer/{trainerId}") // Bazowa ścieżka URL dla operacji uwierzytelniania
public class TrainerClientController {
    private final ClientService clientService;
    private final TrainerService trainerService;


    public TrainerClientController(ClientService clientService, TrainerService trainerService) {
        this.trainerService = trainerService;
        this.clientService = clientService;
    }

    // Pobierz wszystkich klientów dla danego trenera :)
    @GetMapping ("/clients") //
    public ResponseEntity<List<ClientDTO>> getAllClientsByTrainerId(@PathVariable Long trainerId) {
        List<ClientDTO> clients = clientService.getClientsDtoByTrainerId(trainerId);
        return ResponseEntity.ok(clients);
    }

    // Przypisz klienta do trenera
    @PostMapping("/assign/{clientId}")
    public ResponseEntity<ClientDTO> assignClientToTrainer(
            @PathVariable Long trainerId,
            @PathVariable Long clientId) {
        try {
            ClientDTO updatedClient = trainerService.assignClient(trainerId, clientId);
            return ResponseEntity.ok(updatedClient); // 200 OK
        } catch (IllegalArgumentException e) {
            // Rzucone gdy Trener lub Klient nie istnieje
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (IllegalStateException e) {
            // Rzucone gdy Klient ma już innego trenera
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
        }
    }

    @DeleteMapping("/unassign/{clientId}")
    public ResponseEntity<Void> unassignClient(
            @PathVariable Long trainerId,
            @PathVariable Long clientId){
        try {
            // Logika odpinania jest w Serwisie Trenera
            trainerService.unassignClient(clientId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}
