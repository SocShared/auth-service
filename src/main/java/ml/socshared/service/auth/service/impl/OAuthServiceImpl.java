package ml.socshared.service.auth.service.impl;

import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.request.AuthRequest;
import ml.socshared.service.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.service.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.service.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.entity.User;
import ml.socshared.service.auth.exception.impl.AuthenticationException;
import ml.socshared.service.auth.repository.UserRepository;
import ml.socshared.service.auth.service.ClientService;
import ml.socshared.service.auth.service.OAuthService;
import ml.socshared.service.auth.service.UserService;
import ml.socshared.service.auth.service.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ClientService clientService;

    @Override
    public OAuth2TokenResponse getTokenByUsernameAndPassword(OAuthFlowRequest request) {
        log.info("getting token by password grant type");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(request.getUsername());
        authRequest.setPassword(request.getPassword());

        ClientCredentialsRequest clientCredentialsRequest = ClientCredentialsRequest.builder()
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .build();

        if (userService.checkData(authRequest).getSuccess() && clientService.checkData(clientCredentialsRequest).getSuccess()) {
            User user = userRepository.findByUsername(request.getUsername()).orElse(new User());
            Client client = clientService.findById(request.getClientId());

            return jwtTokenProvider.createTokenByUsernameAndPassword(user, client);
        }
        throw new AuthenticationException("Authentication failed");
    }

    @Override
    public OAuth2TokenResponse getTokenByRefreshToken(OAuthFlowRequest request) {
        return null;
    }

    @Override
    public OAuth2TokenResponse getTokenByAuthorizationCode(OAuthFlowRequest request) {
        return null;
    }

    @Override
    public OAuth2TokenResponse getTokenByClientCredentials(OAuthFlowRequest request) {
        return null;
    }
}
