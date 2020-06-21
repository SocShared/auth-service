package ml.socshared.auth.repository;

import ml.socshared.auth.domain.model.ClientModel;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.entity.base.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByClientIdAndClientSecret(UUID clientId, UUID clientSecret);
    @Query("select c from Client c where c.clientId = :clientId and c.user.userId = :userId")
    Optional<Client> findByClientIdAndUserId(@Param("clientId") UUID clientId, @Param("userId") UUID userId);
    @Query("select c from Client c where c.user.userId = :userId")
    Page<Client> findByUserId(UUID userId, Pageable pageable);
    @Query("select c from Client c where c.status = 'ACTIVE' or c.status = 'NOT_ACTIVE'")
    Page<Client> findAllClients(Pageable pageable);
    @Query("select c from Client c where c.status = 'ACTIVE'")
    Page<Client> findActiveClients(Pageable pageable);
    @Query("select c from Client c where c.status = 'NOT_ACTIVE'")
    Page<Client> findNotActiveClients(Pageable pageable);
    @Query("select c from Client c where c.status = 'DELETE'")
    Page<Client> findDeletedClients(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Client c set c.status = :status where c.clientId = :id")
    Optional<Client> setStatus(@Param("id") UUID id, @Param("status") Status status);
}
