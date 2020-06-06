package ml.socshared.auth.repository;

import ml.socshared.auth.entity.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, String> {

    AuthorizationCode findByUserId(UUID id);

}
