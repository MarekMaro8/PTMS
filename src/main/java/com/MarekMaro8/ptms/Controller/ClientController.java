package com.MarekMaro8.ptms.Controller;

import com.MarekMaro8.ptms.model.Client;
import com.MarekMaro8.ptms.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 1. Oznacza, że ta klasa obsługuje żądania HTTP
@RequestMapping("/api/clients") // 2. Definiuje bazową ścieżkę URL
public class ClientController {

    private final ClientService clientService;

    // Wstrzyknięcie ClientService
    //@Autowired // Opcjonalne, ponieważ jest tylko jeden konstruktor
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }



//    @GetMapping // 4. Mapuje żądania GET do /api/clients
//    public ResponseEntity<List<Client>> getAllClients() {
//        List<Client> clients = clientService.findAllClients();
//        // Zwraca listę klientów i status 200 OK
//        return ResponseEntity.ok(clients);
//    }


}

