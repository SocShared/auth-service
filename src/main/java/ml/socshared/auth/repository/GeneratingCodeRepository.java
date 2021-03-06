package ml.socshared.auth.repository;

import ml.socshared.auth.entity.GeneratingCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratingCodeRepository extends JpaRepository<GeneratingCode, String> {
}
