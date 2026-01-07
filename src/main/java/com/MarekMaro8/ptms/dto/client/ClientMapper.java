package com.MarekMaro8.ptms.dto.client;
import com.MarekMaro8.ptms.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    //entity to dto
    public ClientDTO toDto(Client client) {
        String trainerName = "Brak";
        String trainerEmail = "Brak";
        Long trainerId = null;
        if (client.getTrainer() != null) {
            trainerName = client.getTrainer().getFirstName() + " " + client.getTrainer().getLastName();
            trainerEmail = client.getTrainer().getEmail();
            trainerId = client.getTrainer().getId();
        }

        // Tutaj "rozpakowujemy" encję do bezpiecznego pudełka DTO
        return new ClientDTO(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getHealthStatus(),
                trainerName,
                trainerEmail,
                trainerId,
                client.getNotes()
        );
    }
    //dto to entity
    public Client toEntity(ClientRegistrationDTO registrationDto) {
        Client client = new Client();
        client.setFirstName(registrationDto.firstName());
        client.setLastName(registrationDto.lastName());
        client.setEmail(registrationDto.email());
        client.setPassword(registrationDto.password());

        return client;
    }

}
