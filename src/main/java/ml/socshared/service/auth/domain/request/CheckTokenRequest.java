package ml.socshared.service.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class CheckTokenRequest {

    private UUID fromServiceId;
    private UUID toServiceId;
    private String token;

}