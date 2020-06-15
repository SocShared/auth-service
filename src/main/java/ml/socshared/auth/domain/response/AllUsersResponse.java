package ml.socshared.auth.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllUsersResponse {

    private Long allUsers;

}
