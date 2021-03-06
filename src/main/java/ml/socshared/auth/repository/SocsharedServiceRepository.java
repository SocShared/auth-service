package ml.socshared.auth.repository;

import ml.socshared.auth.entity.SocsharedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocsharedServiceRepository extends JpaRepository<SocsharedService, UUID> {

    Optional<SocsharedService> findByServiceIdAndServiceSecret(UUID serviceId, UUID serviceSecret);

}
