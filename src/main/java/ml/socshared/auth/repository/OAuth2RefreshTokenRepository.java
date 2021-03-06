package ml.socshared.auth.repository;

import ml.socshared.auth.entity.OAuth2RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OAuth2RefreshTokenRepository extends JpaRepository<OAuth2RefreshToken, UUID> {
}
