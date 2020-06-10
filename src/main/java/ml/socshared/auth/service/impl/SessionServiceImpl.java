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
        Integer online = sessionRepository.countOnline(time);
        Integer active = sessionRepository.activeUsers(time);
        Integer newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(5));
        Integer allUsers = userRepository.countAll();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("online", online);
        sentrySender.sentryMessage("online users = " + online, additionalData, Collections.singletonList(SentryTag.ONLINE_USERS));
        additionalData = new HashMap<>();
        additionalData.put("active", active);
        sentrySender.sentryMessage("active users = " + active, additionalData, Collections.singletonList(SentryTag.ACTIVE_USERS));
        additionalData.put("new_users", newUsers);
        sentrySender.sentryMessage("new users = " + newUsers, additionalData, Collections.singletonList(SentryTag.NEW_USERS));
        additionalData.put("all_users", newUsers);
        sentrySender.sentryMessage("all users = " + allUsers, additionalData, Collections.singletonList(SentryTag.ALL_USERS));
    }

}
