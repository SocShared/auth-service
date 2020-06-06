package ml.socshared.auth.service.impl;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.request.AuthRequest;
import ml.socshared.auth.domain.request.CheckTokenRequest;
import ml.socshared.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.exception.impl.AuthenticationException;
import ml.socshared.auth.repository.ClientRepository;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.service.ClientService;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import ml.socshared.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.auth.service.OAuthService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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
    public OAuth2TokenResponse getTokenByUsernameAndPassword(OAuthFlowRequest request) throws AuthenticationException {
        log.info("getting token by password grant type");

        if (request.getUsername() == null)
            throw new AuthenticationException("username is invalid");

        if (request.getPassword() == null)
            throw new AuthenticationException("password is invalid");

        if (request.getClientId() == null)
            throw new AuthenticationException("client_id is invalid");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(request.getUsername());
        authRequest.setPassword(Hashing.sha256().hashString(request.getPassword(), StandardCharsets.UTF_8).toString());

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

        if ((client.getAccessType() == Client.AccessType.CONFIDENTIAL &&
                !clientService.checkData(clientCredentialsRequest).getSuccess()) || response == null) {
            log.warn("request -> {}", request.toString());
            throw new AuthenticationException("Authentication failed");
        }

        return response;
    }

    @Override
    public OAuth2TokenResponse getTokenByRefreshToken(OAuthFlowRequest request) {
        log.info("getting token by refresh grant type");

        if (request.getClientId() == null)
            throw new AuthenticationException("client_id is invalid");

        if (request.getRefreshToken() == null)
            throw new AuthenticationException("refresh_token is invalid");

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
            log.warn("request -> {}", request);
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

        if (request.getClientId() == null)
            throw new AuthenticationException("client_id is invalid");

        if (request.getClientSecret() == null)
            throw new AuthenticationException("client_secret is invalid");

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

        log.info("request -> {}", request);
        throw new AuthenticationException("Authentication failed");

    }

    @Override
    public SuccessResponse checkValidateAccessToken(CheckTokenRequest request) {
        log.info("checking data service to");

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(jwtTokenProvider.validateAccessToken(request.getToken()));

        return successResponse;
    }
}
