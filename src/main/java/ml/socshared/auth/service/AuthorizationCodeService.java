package ml.socshared.auth.service;

import ml.socshared.auth.entity.AuthorizationCode;

import java.util.UUID;

public interface AuthorizationCodeService {

    AuthorizationCode findById(String code);
    AuthorizationCode generationCode(UUID userId, UUID clientId, String redirectUri);
    boolean validateCode(String code);

}
