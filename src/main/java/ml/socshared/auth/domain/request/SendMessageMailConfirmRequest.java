package ml.socshared.auth.domain.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class SendMessageMailConfirmRequest {

    @NotNull
    private String subject;
    @NotNull
    private String username;
    @NotNull
    private String link;
    @NotNull
    @Email
    private String toEmail;

}
