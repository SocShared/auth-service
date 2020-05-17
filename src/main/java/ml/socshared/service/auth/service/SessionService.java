package ml.socshared.service.auth.service;

import ml.socshared.service.auth.entity.Session;

import java.util.UUID;

public interface SessionService {

    Session save(Session session);
    Session findByClientIdAndUserId(UUID clientId, UUID userId);
    void deleteById(UUID sessionId);

}
