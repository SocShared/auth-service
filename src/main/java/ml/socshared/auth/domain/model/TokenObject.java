package ml.socshared.auth.domain.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TokenObject {

    private String token;

}
