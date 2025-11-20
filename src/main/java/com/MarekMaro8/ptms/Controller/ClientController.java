package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // Do uruchomienia walidacji hasła (@Size)
import com.MarekMaro8.ptms.dto.LoginRequest;
import java.util.List;
import java.util.NoSuchElementException;

@RestController // 1. Oznacza, że ta klasa obsługuje żądania HTTP
@RequestMapping("/api/clients") // 2. Definiuje bazową ścieżkę URL
public class ClientController {

    private final ClientService clientService;

    // Wstrzyknięcie ClientService
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Endpoint: POST /api/clients
     * Używany do tworzenia nowego klienta (np. rejestracji).
     */
    @PostMapping // 3. Mapuje żądania POST do /api/clients
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        try {
            Client savedClient = clientService.saveClient(client);
            // Zwraca klienta i status 201 Created
            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // W przypadku, gdy e-mail już istnieje, zwracamy 409 Conflict
            // Lepszym rozwiązaniem byłoby użycie @ControllerAdvice, ale to na później
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    /**
     * Endpoint: GET /api/clients
     * Używany do pobrania listy wszystkich klientów.
     */
    @GetMapping // 4. Mapuje żądania GET do /api/clients
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.findAllClients();
        // Zwraca listę klientów i status 200 OK
        return ResponseEntity.ok(clients);
    }


    /**
     * Endpoint: GET /api/clients/login
     * Używany do zalogowania dla klientów.
     */

    @PostMapping("/login")
    public ResponseEntity<Client> loginClient(@RequestBody LoginRequest loginRequest) {
        try {
            Client client = clientService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(client);
        } catch (IllegalArgumentException e) {
            // Jeśli hasło lub email są złe, zwracamy 401 Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

