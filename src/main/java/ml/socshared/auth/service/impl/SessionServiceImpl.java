package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.service.SessionService;
import ml.socshared.auth.entity.Session;
import ml.socshared.auth.repository.SessionRepository;
import ml.socshared.auth.service.sentry.SentrySender;
import ml.socshared.auth.service.sentry.SentryTag;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final UserRepository userRepository;
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
        long online = sessionRepository.countOnline(time);
        long active = sessionRepository.activeUsers(time);
        long newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(5));
        long allUsers = userRepository.count();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("online_users", online);
        additionalData = new HashMap<>();
        additionalData.put("active_users", active);
        additionalData.put("new_users", newUsers);
        additionalData.put("all_users", allUsers);

        sentrySender.sentryMessage("metrics users", additionalData, Collections.singletonList(SentryTag.METRICS_USERS));
    }

}
