package ml.socshared.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ServiceTokenRequest {

    private UUID fromServiceId;
    private UUID toServiceId;
    private UUID toSecretService;

}
