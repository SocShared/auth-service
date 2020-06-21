package ml.socshared.auth.domain.response.stat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActiveUsersResponse {

    private Long activeUsers;

}
