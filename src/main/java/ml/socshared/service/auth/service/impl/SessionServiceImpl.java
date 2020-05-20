package ml.socshared.service.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.entity.Session;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.SessionRepository;
import ml.socshared.service.auth.service.SessionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public Session findByClientIdAndUserId(UUID clientId, UUID userId) {
        log.info("find by clientId -> {}, userId -> {}", clientId, userId);
        return sessionRepository.findSessionByClientIdAndUserId(clientId, userId).orElse(null);
    }

    @Override
    public Session findById(UUID id) {
        log.info("find by id -> {}", id);
        return sessionRepository.findById(id).orElse(null);
    }

    @Override
    public Session save(Session session) {
        log.info("saving session -> {}", session.getSessionId());
        return sessionRepository.save(session);
    }

    @Override
    public void deleteById(UUID sessionId) {
        log.info("deleting session -> {}", sessionId);
        sessionRepository.deleteById(sessionId);
    }


}
