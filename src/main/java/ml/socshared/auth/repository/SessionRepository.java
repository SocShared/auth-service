package ml.socshared.auth.repository;

import ml.socshared.auth.entity.Session;
import ml.socshared.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    @Query("select s from Session s where s.client.clientId = :clientId and s.user.userId = :userId")
    Optional<Session> findSessionByClientIdAndUserId(@Param("clientId") UUID clientId, @Param("userId") UUID userId);

    @Query("select count(s) from Session s where s.accessToken.expireIn >= :dateLong")
    long countOnline(@Param("dateLong") Long dateLong);

    @Query("select count(s) from Session s where s.refreshToken.refreshExpiresIn >= :dateLong")
    long activeUsersCount(@Param("dateLong") Long dateLong);

    @Query("select distinct u from User u, Session s where s.refreshToken.refreshExpiresIn >= :dateLong and u = s.user")
    Page<User> activeUsers(@Param("dateLong") Long dateLong, Pageable pageable);

}
