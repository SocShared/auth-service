package ml.socshared.auth.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.model.TokenObject;
import ml.socshared.auth.domain.request.ServiceTokenRequest;
import ml.socshared.auth.entity.ServiceToken;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@Aspect
public class TokenGetter {

    private final JwtTokenProvider jwtTokenProvider;

    private TokenObject tokenMail;

    public TokenGetter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        init();
    }

    private void init() {
        this.tokenMail = new TokenObject();
    }

    @Before("execution(* ml.socshared.auth.service.impl.*.*(..))")
    public TokenObject getTokenMail() {
        if (tokenMail.getToken() != null && jwtTokenProvider.validateServiceToken(tokenMail.getToken())) {
            return tokenMail;
        }

        ServiceTokenRequest request = new ServiceTokenRequest();
        request.setFromServiceId(UUID.fromString("58c2b3d5-dfad-41af-9451-d0a26fdc9019"));
        request.setToServiceId(UUID.fromString("68c5c6d9-fb18-4adb-800e-faac3ac745b9"));
        request.setToSecretService(UUID.fromString("a981045d-e269-4b28-b7b7-af4a885b9dc4"));

        this.tokenMail.setToken(jwtTokenProvider.buildServiceToken(request).getToken());

        return tokenMail;
    }

}
