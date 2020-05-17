package ml.socshared.service.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.model.UserModel;
import ml.socshared.service.auth.domain.request.AuthRequest;
import ml.socshared.service.auth.domain.request.NewUserRequest;
import ml.socshared.service.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.service.auth.domain.request.UpdateUserRequest;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.User;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.entity.base.Status;
import ml.socshared.service.auth.exception.impl.EmailIsExistsException;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.exception.impl.UsernameAndEmailIsExistsException;
import ml.socshared.service.auth.exception.impl.UsernameIsExistsException;
import ml.socshared.service.auth.repository.UserRepository;
import ml.socshared.service.auth.repository.RoleRepository;
import ml.socshared.service.auth.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponse add(NewUserRequest request) {
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
        user.setPassword(request.getPassword());

        Role role = roleRepository.findByName("CONTENT_MANAGER").orElse(null);
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        return new UserResponse(userRepository.save(user));
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
        return SuccessResponse.builder().success(true).build();
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
        return SuccessResponse.builder().success(user != null).build();
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
}
