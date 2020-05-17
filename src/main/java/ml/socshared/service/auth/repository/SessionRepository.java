package ml.socshared.service.auth.repository;

import ml.socshared.service.auth.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    @Query("select s from Session s where s.client.clientId = :clientId and s.user.userId = :userId")
    Optional<Session> findSessionByClientIdAndUserId(@Param("clientId") UUID clientId, @Param("userId") UUID userId);

}
