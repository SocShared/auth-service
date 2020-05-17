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
import ml.socshared.service.auth.repository.ClientRepository;
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
    private final ClientRepository clientRepository;
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

        Client client = clientRepository.findById(UUID.fromString(request.getClientId())).orElseThrow(
                () -> new AuthenticationException("client_id invalid")
        );

        if (client.getAccessType() == Client.AccessType.BEARER_ONLY)
            throw new AuthenticationException("Invalid flow");

        OAuth2TokenResponse response = null;
        if (client.getAccessType() == Client.AccessType.PUBLIC) {
            if (userService.checkData(authRequest).getSuccess()) {
                User user = userRepository.findByUsername(request.getUsername()).orElse(new User());
                response = jwtTokenProvider.createTokenByUsernameAndPassword(user, client);
            }
        }

        if ((client.getAccessType() == Client.AccessType.CONFIDENTIAL && !clientService.checkData(clientCredentialsRequest).getSuccess())
                || response == null) {
            throw new AuthenticationException("Authentication failed");
        }

        return response;
    }

    @Override
    public OAuth2TokenResponse getTokenByRefreshToken(OAuthFlowRequest request) {
        log.info("getting token by refresh grant type");

        ClientCredentialsRequest clientCredentialsRequest = ClientCredentialsRequest.builder()
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .build();

        Client client = clientRepository.findById(UUID.fromString(request.getClientId())).orElseThrow(
                () -> new AuthenticationException("client_id invalid")
        );

        OAuth2TokenResponse response = null;
        if (jwtTokenProvider.validateRefreshToken(request.getRefreshToken())) {
            response = jwtTokenProvider.createTokenByRefreshToken(request.getRefreshToken(), client);
        }

        if ((client.getAccessType() == Client.AccessType.CONFIDENTIAL && !clientService.checkData(clientCredentialsRequest).getSuccess())
                || response == null) {
            throw new AuthenticationException("Authentication failed");
        }

        return response;
    }

    @Override
    public OAuth2TokenResponse getTokenByAuthorizationCode(OAuthFlowRequest request) {
        return null;
    }

    @Override
    public OAuth2TokenResponse getTokenByClientCredentials(OAuthFlowRequest request) {
        log.info("getting token by client credential grant type");

        ClientCredentialsRequest clientCredentialsRequest = ClientCredentialsRequest.builder()
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .build();

        Client client = clientRepository.findById(UUID.fromString(request.getClientId())).orElseThrow(
                () -> new AuthenticationException("client_id invalid")
        );

        if (client.getAccessType() == Client.AccessType.BEARER_ONLY && clientService.checkData(clientCredentialsRequest).getSuccess()) {
            OAuth2TokenResponse response = null;
            if (client.getAccessType() == Client.AccessType.PUBLIC) {
                if (jwtTokenProvider.validateRefreshToken(request.getRefreshToken())) {
                    response = jwtTokenProvider.createTokenByRefreshToken(request.getRefreshToken(), client);
                }
            }
            return response;
        }

        throw new AuthenticationException("Authentication failed");

    }
}
