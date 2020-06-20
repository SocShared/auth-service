package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.entity.AuthorizationCode;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.repository.AuthorizationCodeRepository;
import ml.socshared.auth.service.AuthorizationCodeService;
import ml.socshared.auth.util.GeneratorLinks;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    private final AuthorizationCodeRepository repository;

    @Override
    public AuthorizationCode findById(String code) {
        return repository.findById(code)
                .orElse(null);
    }

    public AuthorizationCode generationCode(UUID userId, UUID clientId, String redirectUri) {
        AuthorizationCode code = new AuthorizationCode();
        code.setGeneratingLink(GeneratorLinks.build());
        code.setUserId(userId);
        code.setClientId(clientId);
        code.setRedirectUri(redirectUri);
        code.setExpireIn(LocalDateTime.now().plusMinutes(5));
        return repository.saveAndFlush(code);
    }

    public boolean validateCode(String code) {
        AuthorizationCode authorizationCode = findById(code);

        if (authorizationCode != null)
            repository.deleteById(authorizationCode.getGeneratingLink());

        return authorizationCode != null && !authorizationCode.getExpireIn().isBefore(LocalDateTime.now());
    }
}
