package ml.socshared.service.auth.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ServiceTokenResponse {

    private String token;
    private String toService;
    private String fromService;
    private Long expireIn;

}
