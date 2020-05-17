package ml.socshared.service.auth.domain.request;

import lombok.*;

@Builder
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class ClientCredentialsRequest {

    private String clientId;
    private String clientSecret;

}
