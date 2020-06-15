package ml.socshared.auth.service;

import ml.socshared.auth.domain.response.ActiveUsersResponse;
import ml.socshared.auth.entity.Session;

import java.util.UUID;

public interface SessionService {

    Session save(Session session);
    Session findById(UUID id);
    Session findByClientIdAndUserId(UUID clientId, UUID userId);
    ActiveUsersResponse activeUsers();
    void deleteById(UUID sessionId);

}
