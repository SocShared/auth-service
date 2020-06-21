package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.response.ActiveUsersResponse;
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

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
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

    @Override
    public ActiveUsersResponse activeUsers() {
        long time = new Date().getTime();
        log.info("time long -> {}", time);
        Long active = sessionRepository.activeUsers(time);
        log.info("active users -> {}", active);
        return ActiveUsersResponse.builder()
                .activeUsers(active)
                .build();
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void onlineUsersStat() {
        long onlineUsers = userRepository.countByTimeOnlineAfter(LocalDateTime.now().minusDays(1));
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("online_users", onlineUsers);

        sentrySender.sentryMessage("online users", additionalData, Collections.singletonList(SentryTag.ONLINE_USERS));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void activeUsersStat() {
        long time = new Date().getTime();
        long active = sessionRepository.activeUsers(time);
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("active_users", active);
        sentrySender.sentryMessage("active users", additionalData, Collections.singletonList(SentryTag.ACTIVE_USERS));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void newUsersStat() {
        long newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(5));
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("new_users", newUsers);

        sentrySender.sentryMessage("new users", additionalData, Collections.singletonList(SentryTag.NEW_USERS));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void allUsersStat() {
        long allUsers = userRepository.count();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("all_users", allUsers);

        sentrySender.sentryMessage("all users", additionalData, Collections.singletonList(SentryTag.ALL_USERS));
    }
}
