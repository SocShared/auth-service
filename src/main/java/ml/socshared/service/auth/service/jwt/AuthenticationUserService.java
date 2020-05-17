package ml.socshared.service.auth.service.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.exception.impl.AuthenticationException;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationUserService {

    private final UserService userService;

    public UserResponse loadUserByUsername(String username) {
        UserResponse user;
        try {
            user = userService.findByUsername(username);
        } catch (HttpNotFoundException notFoundByUsernameExc) {
            try {
                user = userService.findByEmail(username);
            } catch (HttpNotFoundException notFoundByEmailExc) {
                throw new AuthenticationException("Not found user by username or email: " + username);
            }
        }

        if (user == null)
            throw new AuthenticationException("Not found User with username or email: " + username);

        log.info("LOAD BY USERNAME - user with username: {} successfully loaded", username);
        return user;
    }
}
