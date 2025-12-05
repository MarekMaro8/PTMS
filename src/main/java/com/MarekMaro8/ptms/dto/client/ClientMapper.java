package com.MarekMaro8.ptms.dto.client;
import com.MarekMaro8.ptms.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    //entity to dto
    public ClientDTO toDto(Client client) {
        String trainerName = "Brak";
        String trainerEmail = "Brak";
        if (client.getTrainer() != null) {
            trainerName = client.getTrainer().getFirstName() + " " + client.getTrainer().getLastName();
            trainerEmail = client.getTrainer().getEmail();
        }

        // Tutaj "rozpakowujemy" encję do bezpiecznego pudełka DTO
        return new ClientDTO(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                trainerName,
                trainerEmail
        );
    }
    //dto to entity
    public Client toEntity(ClientRegistrationDTO registrationDto) {
        Client client = new Client();
        client.setFirstName(registrationDto.getFirstName());
        client.setLastName(registrationDto.getLastName());
        client.setEmail(registrationDto.getEmail());
        client.setPassword(registrationDto.getPassword());

        return client;
    }

}
