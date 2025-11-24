package com.MarekMaro8.ptms.repository;
import com.MarekMaro8.ptms.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends  JpaRepository<Trainer, Long>{

    Optional<Trainer> findByEmail(String email);
    List<Trainer> findByClients_Id(Long clientId);

}
