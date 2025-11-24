package com.MarekMaro8.ptms.repository;

import com.MarekMaro8.ptms.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByClientIdOrderByStartTimeDesc(Long clientId);
}
