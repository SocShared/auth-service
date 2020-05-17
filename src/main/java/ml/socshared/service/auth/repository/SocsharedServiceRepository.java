package ml.socshared.service.auth.repository;

import ml.socshared.service.auth.entity.SocsharedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SocsharedServiceRepository extends JpaRepository<SocsharedService, UUID> {
}
