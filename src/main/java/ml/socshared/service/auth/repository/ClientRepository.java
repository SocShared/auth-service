package ml.socshared.service.auth.repository;

import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query("select c from Client c where c.clientId = :clientId and c.clientSecret = :clientSecret")
    Optional<Client> findByClientIdAndClientSecret(@Param("clientId") String clientId, @Param("clientSecret") String clientSecret);
    @Query("select c from Client c where c.clientId = :clientId")
    Optional<Client> findByClientId(@Param("clientId") String clientId);
}
