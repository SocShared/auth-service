package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.response.stat.ActiveUsersResponse;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.service.SessionService;
import ml.socshared.auth.entity.Session;
import ml.socshared.auth.repository.SessionRepository;
import ml.socshared.auth.service.sentry.SentrySender;
import ml.socshared.auth.service.sentry.SentryTag;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ActiveUsersResponse activeUsersCount() {
        long time = new Date().getTime();
        Long active = sessionRepository.activeUsersCount(time);
        log.info("active users -> {}", active);
        return ActiveUsersResponse.builder()
                .activeUsers(active)
                .build();
    }

    @Override
    public Page<User> getActiveUsers(Integer page, Integer size) {
        long time = new Date().getTime();
        return sessionRepository.activeUsers(time, PageRequest.of(page, size));
    }

    @Scheduled(cron = "0 0 20 * * *")
    @SchedulerLock(name = "online-users-stat")
    public void onlineUsersStat() {
        long onlineUsers = userRepository.countByTimeOnlineAfter(LocalDateTime.now().minusDays(1));
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("online_users", onlineUsers);

        sentrySender.sentryMessage("online users", additionalData, Collections.singletonList(SentryTag.ONLINE_USERS));
    }

    @Scheduled(cron = "0 0 14 * * *")
    @SchedulerLock(name = "active-users-stat")
    public void activeUsersStat() {
        long time = new Date().getTime();
        long active = sessionRepository.activeUsersCount(time);
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("active_users", active);
        sentrySender.sentryMessage("active users", additionalData, Collections.singletonList(SentryTag.ACTIVE_USERS));
    }

    @Scheduled(cron = "0 0 14 * * *")
    @SchedulerLock(name = "new-users-stat")
    public void newUsersStat() {
        long newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(5));
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("new_users", newUsers);

        sentrySender.sentryMessage("new users", additionalData, Collections.singletonList(SentryTag.NEW_USERS));
    }

    @Scheduled(cron = "0 0 14 * * *")
    @SchedulerLock(name = "all-users-stat")
    public void allUsersStat() {
        long allUsers = userRepository.count();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("all_users", allUsers);

        sentrySender.sentryMessage("all users", additionalData, Collections.singletonList(SentryTag.ALL_USERS));
    }
}
