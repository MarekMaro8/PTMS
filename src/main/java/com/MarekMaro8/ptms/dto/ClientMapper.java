package com.MarekMaro8.ptms.dto;
import com.MarekMaro8.ptms.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDTO toDto(Client client) {
        String trainerName = "Brak";
        if (client.getTrainer() != null) {
            trainerName = client.getTrainer().getFirstName() + " " + client.getTrainer().getLastName();
        }

        // Tutaj "rozpakowujemy" encję do bezpiecznego pudełka DTO
        return new ClientDTO(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                trainerName
        );
    }
}
