package ml.socshared.service.auth.repository;

import ml.socshared.service.auth.entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, UUID> {

    Optional<Scope> findByName(String name);

}
