package com.MarekMaro8.ptms.dto.trainer;

import com.MarekMaro8.ptms.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {
    public TrainerDTO toDto(Trainer trainer) {

        return new TrainerDTO(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getEmail()
        );
    }

    public Trainer toEntity(TrainerRegistrationDTO trainerRegistrationDTO) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(trainerRegistrationDTO.firstName());
        trainer.setLastName(trainerRegistrationDTO.lastName());
        trainer.setEmail(trainerRegistrationDTO.email());
        trainer.setPassword(trainerRegistrationDTO.password());
        return trainer;
    }


}
