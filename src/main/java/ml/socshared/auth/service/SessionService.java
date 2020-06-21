package ml.socshared.auth.service;

import ml.socshared.auth.domain.response.stat.ActiveUsersResponse;
import ml.socshared.auth.entity.Session;
import ml.socshared.auth.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SessionService {

    Session save(Session session);
    Session findById(UUID id);
    Session findByClientIdAndUserId(UUID clientId, UUID userId);
    ActiveUsersResponse activeUsersCount();
    Page<User> getActiveUsers(Integer page, Integer size);
    void deleteById(UUID sessionId);

}
