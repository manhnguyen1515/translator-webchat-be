package com.translator.webchat.repositories;

import com.translator.webchat.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SessionUsersRepository extends JpaRepository<Session, Long> {
    //@Query(value = "SELECT s.session_id FROM session_users s WHERE s.user_id = :userId1 " AND EXISTS SELECT 1 FROM session_users s2 WHERE s2.session_id = s1.session_id AND s2.user_id = :userId2", nativeQuery = true)
    @Query(value = "SELECT s1.session_id FROM session_users s1 WHERE s1.user_id = :userId1  AND EXISTS (SELECT 1 FROM session_users s2 WHERE s2.session_id = s1.session_id AND s2.user_id = :userId2)", nativeQuery = true)
    Optional<Long> findSessionIdsByUserId(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}