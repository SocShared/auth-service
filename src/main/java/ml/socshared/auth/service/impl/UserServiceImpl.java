package ml.socshared.auth.service.impl;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.client.MailSenderClient;
import ml.socshared.auth.domain.model.UserModel;
import ml.socshared.auth.domain.request.*;
import ml.socshared.auth.domain.request.*;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.entity.GeneratingCode;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.entity.Role;
import ml.socshared.auth.entity.base.Status;
import ml.socshared.auth.exception.impl.EmailIsExistsException;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.exception.impl.UsernameAndEmailIsExistsException;
import ml.socshared.auth.exception.impl.UsernameIsExistsException;
import ml.socshared.auth.repository.GeneratingCodeRepository;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.repository.RoleRepository;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.util.GeneratorLinks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final GeneratingCodeRepository generatingCodeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailSenderClient mailSenderClient;

    @Value("${main.host}")
    private String mainHost;

    @Value("#{tokenGetter.getTokenMail()}")
    private String token;

    @Override
    public UserResponse add(NewUserRequest request) {
        log.info("saving -> {}", request);
        System.out.println(token);
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

        String link = GeneratorLinks.build();
        GeneratingCode code = new GeneratingCode();
        code.setGeneratingLink(link);
        code.setUserId(u.getUserId());
        code.setExpireIn(LocalDateTime.now().plusHours(24));
        code.setType(GeneratingCode.Type.EMAIL_CONFIRMATION);

        GeneratingCode c = generatingCodeRepository.save(code);
        mailSenderClient.sendMailConfirm(SendMessageMailConfirmRequest.builder()
                .subject("SocShared - Подтвердите электронную почту")
                .username(user.getUsername())
                .toEmail(u.getEmail())
                .link(mainHost + "account/" + c.getGeneratingLink())
                .build(), token);

        return new UserResponse(u);
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

        user.setPassword(request.getPassword());

        user = userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    public SuccessResponse deleteById(UUID id) {
        log.info("deleting by id -> {}", id);

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setStatus(Status.DELETE);
            user.setEmail(user.getEmail() + "\\" + user.getUserId());
            user.setUsername(user.getUsername() + "\\" + user.getUserId());
            user = userRepository.save(user);
        }
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(true);

        return successResponse;
    }

    @Override
    public UserResponse findById(UUID id) {
        log.info("find by id -> {}", id);
        return new UserResponse(userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));
    }

    @Override
    public UserResponse findByUsername(String username) {
        log.info("find by username -> {}", username);
        return new UserResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by username: " + username)));
    }

    @Override
    public UserResponse findByEmail(String email) {
        log.info("find by email -> {}", email);
        return new UserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by email: " + email)));
    }

    @Override
    public Page<UserModel> findAll(Integer page, Integer size) {
        log.info("find all");
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllUsers(pageable);
    }

    @Override
    public UserResponse activation(UUID id) {
        log.info("activation by id -> {}", id);

        return new UserResponse(userRepository.setStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));
    }

    @Override
    public UserResponse deactivation(UUID id) {
        log.info("deactivating by id -> {}", id);

        return new UserResponse(userRepository.setStatus(id, Status.NOT_ACTIVE)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));
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

        return successResponse;
    }

    @Override
    public UserResponse addRole(UUID id, UUID roleId) {
        log.info("adding role -> {}", roleId);

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new HttpNotFoundException("Not found role by id: " + roleId));

        User user = userRepository.findById(id).orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id));

        user.getRoles().add(role);
        user = userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    public UserResponse removeRole(UUID id, UUID roleId) {
        log.info("removing role -> {}", roleId);

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new HttpNotFoundException("Not found role by id: " + roleId));

        User user = userRepository.findById(id).orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id));

        user.getRoles().remove(role);
        user = userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    public SuccessResponse confirmEmail(String generatingLink) {
        log.info("confirming email");

        GeneratingCode generatingCode = generatingCodeRepository.findById(generatingLink)
                .orElse(null);

        if (generatingCode != null && generatingCode.getType() == GeneratingCode.Type.EMAIL_CONFIRMATION
                && generatingCode.getExpireIn().isAfter(LocalDateTime.now())) {
            User user = userRepository.findById(generatingCode.getUserId()).orElse(null);
            if (user != null) {
                user.setEmailVerified(true);
                generatingCodeRepository.deleteById(generatingLink);
                userRepository.save(user);
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.setSuccess(true);

                return successResponse;
            }

        }
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(false);

        return successResponse;
    }
}
