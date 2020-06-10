package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.service.SessionService;
import ml.socshared.auth.entity.Session;
import ml.socshared.auth.repository.SessionRepository;
import ml.socshared.auth.service.sentry.SentrySender;
import ml.socshared.auth.service.sentry.SentryTag;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SentrySender sentrySender;

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

    @Scheduled(fixedDelay = 120000)
    public void analyzeStatistic() {
        long time = new Date().getTime();
        log.info("time long -> {}", time);
        Integer online = sessionRepository.countOnline(time);
        Integer active = sessionRepository.activeUsers(time);
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("online", online);
        sentrySender.sentryMessage("online = " + online, additionalData, Collections.singletonList(SentryTag.ONLINE_USERS));
        additionalData = new HashMap<>();
        additionalData.put("active", active);
        sentrySender.sentryMessage("active = " + active, additionalData, Collections.singletonList(SentryTag.ACTIVE_USERS));
    }

}
