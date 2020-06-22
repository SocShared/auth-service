package ml.socshared.auth.service.impl;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.client.MailSenderClient;
import ml.socshared.auth.domain.model.TokenObject;
import ml.socshared.auth.domain.request.*;
import ml.socshared.auth.domain.response.stat.AllUsersResponse;
import ml.socshared.auth.domain.response.stat.NewUsersResponse;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.domain.response.stat.OnlineUsersResponse;
import ml.socshared.auth.entity.GeneratingCode;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.entity.Role;
import ml.socshared.auth.exception.impl.EmailIsExistsException;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.exception.impl.UsernameAndEmailIsExistsException;
import ml.socshared.auth.exception.impl.UsernameIsExistsException;
import ml.socshared.auth.repository.GeneratingCodeRepository;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.repository.RoleRepository;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.sentry.SentrySender;
import ml.socshared.auth.service.sentry.SentryTag;
import ml.socshared.auth.util.GeneratorLinks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final GeneratingCodeRepository generatingCodeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailSenderClient mailSenderClient;
    private final SentrySender sentrySender;

    @Value("${main.host}")
    private String mainHost;

    @Value("#{tokenGetter.tokenMail}")
    private TokenObject token;

    @Override
    public UserResponse add(NewUserRequest request) throws UsernameAndEmailIsExistsException, UsernameIsExistsException, EmailIsExistsException {
        log.info("saving -> {}", request);
        boolean isErrorUsername = false;
        if (userRepository.findByUsername(request.getUsername()).orElse(null) != null) {
            isErrorUsername = true;
        }
        boolean isErrorEmail = false;
        if (userRepository.findByEmail(request.getEmail()).orElse(null) != null) {
            isErrorEmail = true;
        }

        if (isErrorUsername && isErrorEmail) {
            throw new UsernameAndEmailIsExistsException();
        } else if (isErrorUsername) {
            throw new UsernameIsExistsException();
        } else if (isErrorEmail) {
            throw new EmailIsExistsException();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPassword(Hashing.sha256().hashString(request.getPassword(), StandardCharsets.UTF_8).toString());

        Role role = roleRepository.findByName("CONTENT_MANAGER").orElse(null);
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        User u = userRepository.save(user);

        sendMailConfirmed(u.getUserId());

        UserResponse userResponse = new UserResponse(u);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_data", userResponse);
        sentrySender.sentryMessage("registration user", additionalData, Collections.singletonList(SentryTag.REGISTRATION_USER));

        return userResponse;
    }

    @Override
    public SuccessResponse sendMailConfirmed(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id"));

        String link = GeneratorLinks.build();
        GeneratingCode code = new GeneratingCode();
        code.setGeneratingLink(link);
        code.setUserId(u.getUserId());
        code.setExpireIn(LocalDateTime.now().plusHours(24));
        code.setType(GeneratingCode.Type.EMAIL_CONFIRMATION);

        GeneratingCode c = generatingCodeRepository.save(code);
        mailSenderClient.sendMailConfirm(SendMessageGeneratingCodeRequest.builder()
                .subject("SocShared - Подтвердите электронную почту")
                .username(u.getUsername())
                .toEmail(u.getEmail())
                .link(mainHost + "account/" + c.getGeneratingLink())
                .build(), "Bearer " + token.getToken());

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(true);

        return successResponse;
    }

    @Override
    public UserResponse update(UUID id, UpdateUserRequest request) {
        log.info("saving -> {}", request);

        User user = userRepository.findById(id).orElseThrow(
                () -> new HttpNotFoundException("Not found user by id: " + id)
        );

        if (userRepository.findByEmail(request.getEmail()).orElse(null) != null) {
            throw new EmailIsExistsException();
        }

        user.setEmail(request.getEmail());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

        user = userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    public UserResponse updatePassword(UUID id, UpdatePasswordRequest request) {
        log.info("saving -> {}", request);

        User user = userRepository.findById(id).orElseThrow(
                () -> new HttpNotFoundException("Not found user by id: " + id)
        );

        user.setPassword(Hashing.sha256().hashString(request.getPassword(), StandardCharsets.UTF_8).toString());
        user.setResetPassword(false);
        user = userRepository.save(user);

        UserResponse response = new UserResponse(user);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_data", response);
        sentrySender.sentryMessage("update user password", additionalData, Collections.singletonList(SentryTag.UPDATE_USER_PASSWORD));

        return response;
    }

    @Override
    public SuccessResponse deleteById(UUID id) {
        log.info("deleting by id -> {}", id);

        userRepository.findById(id).ifPresent(user -> userRepository.deleteById(id));
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(true);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_id", id);
        sentrySender.sentryMessage("delete user by id", additionalData, Collections.singletonList(SentryTag.DELETE_USER_BY_ID));

        return successResponse;
    }

    @Override
    public UserResponse findById(UUID id) {
        log.info("find by id -> {}", id);

        UserResponse userResponse = new UserResponse(userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_data", userResponse);
        sentrySender.sentryMessage("get user by id", additionalData, Collections.singletonList(SentryTag.GET_USER_BY_ID));

        return userResponse;
    }

    @Override
    public UserResponse findByUsername(String username) {
        log.info("find by username -> {}", username);

        UserResponse userResponse = new UserResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by username: " + username)));

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_data", userResponse);
        sentrySender.sentryMessage("get user by username", additionalData, Collections.singletonList(SentryTag.GET_USER_BY_USERNAME));

        return userResponse;
    }

    @Override
    public UserResponse findByEmail(String email) {
        log.info("find by email -> {}", email);

        UserResponse userResponse = new UserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by email: " + email)));

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_data", userResponse);
        sentrySender.sentryMessage("get user by email", additionalData, Collections.singletonList(SentryTag.GET_USER_BY_EMAIL));

        return userResponse;
    }

    @Override
    public Page<User> findAll(Integer page, Integer size) {
        log.info("find all");
        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.findAll(pageable);

        Map<String, Object> additionalData = new HashMap<>();
        sentrySender.sentryMessage("get users", additionalData, Collections.singletonList(SentryTag.GET_USERS));

        return users;
    }

    @Override
    public SuccessResponse checkData(AuthRequest request) {
        log.info("checking username and password");
        Pattern email = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        User user;
        if (email.matcher(request.getUsername()).matches()) {
            user = userRepository.findByEmailAndPassword(request.getUsername(), request.getPassword()).orElse(null);
        } else {
            user = userRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword()).orElse(null);
        }
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(user != null);

        Map<String, Object> additionalData = new HashMap<>();
        sentrySender.sentryMessage("check username and password", additionalData, Collections.singletonList(SentryTag.CHECK_USERNAME_AND_PASSWORD));

        return successResponse;
    }

    @Override
    public UserResponse addRole(UUID id, UUID roleId) {
        log.info("adding role -> {}", roleId);

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new HttpNotFoundException("Not found role by id: " + roleId));

        User user = userRepository.findById(id).orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id));

        user.getRoles().add(role);
        user = userRepository.save(user);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_id", id);
        additionalData.put("role_id", roleId);
        sentrySender.sentryMessage("add role for user", additionalData, Collections.singletonList(SentryTag.ADD_ROLE_FOR_USER));

        return new UserResponse(user);
    }

    @Override
    public UserResponse removeRole(UUID id, UUID roleId) {
        log.info("removing role -> {}", roleId);

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new HttpNotFoundException("Not found role by id: " + roleId));

        User user = userRepository.findById(id).orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id));

        user.getRoles().remove(role);
        user = userRepository.save(user);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_id", id);
        additionalData.put("role_id", roleId);
        sentrySender.sentryMessage("remove role for user", additionalData, Collections.singletonList(SentryTag.REMOVE_ROLE_FOR_USER));

        return new UserResponse(user);
    }

    @Override
    public GeneratingCode processGenerationLink(String generatingLink) {
        log.info("confirming email");

        GeneratingCode generatingCode = generatingCodeRepository.findById(generatingLink)
                .orElse(null);

        if (generatingCode != null && generatingCode.getExpireIn().isAfter(LocalDateTime.now())) {
            User user = userRepository.findById(generatingCode.getUserId()).orElse(null);
            if (user != null) {
                if (generatingCode.getType() == GeneratingCode.Type.EMAIL_CONFIRMATION) {
                    user.setEmailVerified(true);
                } else if (generatingCode.getType() == GeneratingCode.Type.RESET_PASSWORD) {
                    user.setResetPassword(true);
                }
                generatingCodeRepository.deleteById(generatingLink);
                userRepository.save(user);
                return generatingCode;
            }
        }
        return null;
    }

    @Override
    public SuccessResponse resetPassword(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(HttpNotFoundException::new);
        user.setResetPassword(true);
        userRepository.save(user);

        String link = GeneratorLinks.build();
        GeneratingCode code = new GeneratingCode();
        code.setGeneratingLink(link);
        code.setUserId(user.getUserId());
        code.setExpireIn(LocalDateTime.now().plusHours(24));
        code.setType(GeneratingCode.Type.RESET_PASSWORD);

        GeneratingCode c = generatingCodeRepository.save(code);

        mailSenderClient.sendPasswordReset(SendMessageGeneratingCodeRequest.builder()
                .subject("SocShared - Сброс пароля")
                .username(user.getUsername())
                .toEmail(user.getEmail())
                .link(mainHost + "account/" + c.getGeneratingLink())
                .build(), "Bearer " + token.getToken());

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("email", email);
        sentrySender.sentryMessage("reset password", additionalData, Collections.singletonList(SentryTag.RESET_PASSWORD));

        return new SuccessResponse(true);
    }

    @Override
    public OnlineUsersResponse onlineUsersCount() {
        Long onlineUsers = userRepository.countByTimeOnlineAfter(LocalDateTime.now().minusSeconds(30));
        log.info("online users -> {}", onlineUsers);
        return OnlineUsersResponse.builder()
                .onlineUsers(onlineUsers)
                .build();
    }

    @Override
    public Page<User> getOnlineUsers(Integer page, Integer size) {
        return userRepository.findByTimeOnlineAfter(LocalDateTime.now().minusSeconds(30), PageRequest.of(page, size));
    }

    @Override
    public NewUsersResponse newUsersCount() {
        Long newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(5));
        log.info("new users -> {}", newUsers);
        return NewUsersResponse.builder()
                .newUsers(newUsers)
                .build();
    }

    @Override
    public Page<User> getNewUsers(Integer page, Integer size) {
        return userRepository.findByCreatedAtAfter(LocalDateTime.now().minusDays(5), PageRequest.of(page, size));
    }

    @Override
    public AllUsersResponse allUsersCount() {
        Long allUsers = userRepository.count();
        log.info("all users -> {}", allUsers);
        return AllUsersResponse.builder()
                .allUsers(allUsers)
                .build();
    }
}
