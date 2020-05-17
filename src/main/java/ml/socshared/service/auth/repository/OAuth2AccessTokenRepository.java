package ml.socshared.service.auth.repository;

import ml.socshared.service.auth.entity.OAuth2AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OAuth2AccessTokenRepository extends JpaRepository<OAuth2AccessToken, UUID> {
}
