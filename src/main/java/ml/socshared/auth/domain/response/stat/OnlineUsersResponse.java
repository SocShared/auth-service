package ml.socshared.auth.domain.response.stat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnlineUsersResponse {

    private Long onlineUsers;

}
