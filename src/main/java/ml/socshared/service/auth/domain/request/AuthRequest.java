package ml.socshared.service.auth.domain.request;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AuthRequest {

    @NotNull
    private String username;
    @NotNull
    private String password;

}
