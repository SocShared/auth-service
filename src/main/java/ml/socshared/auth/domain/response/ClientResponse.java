package ml.socshared.auth.domain.response;

import lombok.*;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.entity.Role;
import ml.socshared.auth.entity.User;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ClientResponse {

    private UUID clientId;
    private UUID clientSecret;
    private String name;
    private Client.AccessType accessType;
    private String validRedirectUri;
    private Set<Role> roles;
    private User user;

    public ClientResponse() {}

    public ClientResponse(Client client) {
        this.clientId = client.getClientId();
        this.clientSecret = client.getClientSecret();
        this.name = client.getName();
        this.accessType = client.getAccessType();
        this.validRedirectUri = client.getValidRedirectUri();
        this.roles = client.getRoles();
        this.user = client.getUser();
    }

}
