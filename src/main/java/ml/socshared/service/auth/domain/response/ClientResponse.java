package ml.socshared.service.auth.domain.response;

import lombok.*;
import ml.socshared.service.auth.entity.Client;

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
    private Client.AccessType activeType;
    private String validRedirectUri;
    private Set<String> roles;

    public ClientResponse() {}

    public ClientResponse(Client client) {
        this.clientId = client.getClientId();
        this.clientSecret = client.getClientSecret();
        this.name = client.getName();
        this.activeType = client.getAccessType();
        this.validRedirectUri = client.getValidRedirectUri();
        this.roles = new LinkedHashSet<>();
        client.getRoles().forEach(role -> roles.add(role.getName()));
    }

}
