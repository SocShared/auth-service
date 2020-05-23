package ml.socshared.auth.repository;

import ml.socshared.auth.entity.ServiceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ServiceTokenRepository extends JpaRepository<ServiceToken, UUID> {

    @Query("select st from ServiceToken st where st.toServiceId = :toServiceId " +
            "and st.fromService.serviceId = :fromServiceId")
    Optional<ServiceToken> findByToServiceIdAndFromServiceId(@Param("toServiceId") UUID toServiceId,
                                                             @Param("fromServiceId") UUID fromServiceId);
}
