package ml.socshared.auth.repository;

import ml.socshared.auth.domain.model.UserModel;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.entity.base.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Page<User> findByCreatedAtAfter(LocalDateTime localDateTime, Pageable pageable);
    Page<User> findByTimeOnlineAfter(LocalDateTime localDateTime, Pageable pageable);
    @Query("select u from User u where u.status = 'ACTIVE' or u.status = 'NOT_ACTIVE'")
    Page<User> findActiveAndNotActiveUsers(Pageable pageable);
    @Query("select u from User u where u.status = 'ACTIVE'")
    Page<User> findActiveUsers(Pageable pageable);
    @Query("select u from User u where u.status = 'NOT_ACTIVE'")
    Page<User> findNotActiveUsers(Pageable pageable);
    @Query("select u from User u where u.status = 'DELETE'")
    Page<User> findDeletedUsers(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update User u set u.status = :status where u.userId = :id")
    Optional<User> setStatus(@Param("id") UUID id, @Param("status") Status status);

    @Query("select u from User u where u.username = :username and u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Query("select u from User u where u.email = :email and u.password = :password")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    long countByCreatedAtAfter(LocalDateTime localDateTime);
    long countByTimeOnlineAfter(LocalDateTime localDateTime);

}
