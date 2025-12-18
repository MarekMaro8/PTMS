package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.dto.client.ClientDTO;
import com.MarekMaro8.ptms.dto.trainer.TrainerDTO;
import com.MarekMaro8.ptms.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    // 1. Zalogowany trener pobiera SWÓJ profil
    // GET /api/trainers/me
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/me")
    public ResponseEntity<TrainerDTO> getMyProfile(Principal principal) {
        return ResponseEntity.ok(trainerService.getMyProfile(principal.getName()));
    }

    // 2. Zalogowany trener pobiera listę SWOICH klientów
    // GET /api/trainers/me/clients
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/me/clients")
    public ResponseEntity<List<ClientDTO>> getMyClients(Principal principal) {
        return ResponseEntity.ok(trainerService.getMyClients(principal.getName()));
    }

    // 3. Trener pobiera szczegóły konkretnego klienta (z weryfikacją)
    // GET /api/trainers/clients/{clientId}
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/clients/{clientId}")
    public ResponseEntity<ClientDTO> getClientDetails(@PathVariable Long clientId, Principal principal) {
        try {
            return ResponseEntity.ok(trainerService.getMyClientDetails(principal.getName(), clientId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Brak dostępu
        }
    }

    // 4. Przypisz klienta do MNIE (zalogowanego trenera)
    // POST /api/trainers/clients/{clientId}/assign
    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/clients/{clientId}/assign")
    public ResponseEntity<ClientDTO> assignClientToMe(@PathVariable Long clientId, Principal principal) {
        try {
            ClientDTO updatedClient = trainerService.assignClientToMe(principal.getName(), clientId);
            return ResponseEntity.ok(updatedClient);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Klient ma już trenera
        }
    }

    // 5. Odepnij klienta ode MNIE
    // DELETE /api/trainers/clients/{clientId}/unassign
    @PreAuthorize("hasRole('TRAINER')")
    @DeleteMapping("/clients/{clientId}/unassign")
    public ResponseEntity<Void> unassignClientFromMe(@PathVariable Long clientId, Principal principal) {
        try {
            trainerService.unassignClientFromMe(principal.getName(), clientId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Opcjonalnie: Publiczny endpoint do pobrania profilu trenera (bez logowania lub dla klienta)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{trainerId}")
    public ResponseEntity<Optional<TrainerDTO>> getPublicTrainerProfile(@PathVariable Long trainerId) {
        return ResponseEntity.ok(trainerService.getTrainerById(trainerId));
    }
}