package com.translator.webchat.repositories;

import com.translator.webchat.entities.Message;
import com.translator.webchat.entities.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionOrderByIdDesc(Session session, Pageable pageable);

    List<Message> findBySessionOrderByCreatedAtDesc(Session session, Pageable pageable);

    List<Message> findBySessionId(Long sessionId, Pageable pageable);
}
