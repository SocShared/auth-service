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
    @Query("select c from Client c where c.clientId = :clientId and c.clientSecret = :clientSecret")
    Optional<Client> findByClientIdAndClientSecret(@Param("clientId") String clientId, @Param("clientSecret") String clientSecret);
    @Query("select c from Client c where c.clientId = :clientId")
    Optional<Client> findByClientId(@Param("clientId") String clientId);
    @Query("select c from Client c where c.clientId = :clientId and c.user.userId = :userId")
    Optional<Client> findByClientIdAndUserId(@Param("clientId") String clientId, @Param("userId") String userId);
    @Query("select c from Client c where c.user.userId = :userId")
    Page<ClientModel> findByUserId(String userId, Pageable pageable);
    @Query("select c from Client c where c.status = 'ACTIVE' or c.status = 'NOT_ACTIVE'")
    Page<ClientModel> findAllClients(Pageable pageable);
    @Query("select c from Client c where c.status = 'ACTIVE'")
    Page<ClientModel> findActiveClients(Pageable pageable);
    @Query("select c from Client c where c.status = 'NOT_ACTIVE'")
    Page<ClientModel> findNotActiveClients(Pageable pageable);
    @Query("select c from Client c where c.status = 'DELETE'")
    Page<ClientModel> findDeletedClients(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Client c set c.status = :status where c.clientId = :id")
    Optional<Client> setStatus(@Param("id") UUID id, @Param("status") Status status);
}
