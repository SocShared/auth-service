package ml.socshared.service.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ml.socshared.service.auth.entity.base.BaseEntity;
import ml.socshared.service.auth.entity.base.Status;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "oauth2_client_details")
@ToString
@EqualsAndHashCode(callSuper = false)
public class Client extends BaseEntity {

    @Id
    @Column(name = "client_id", nullable = false, unique = true)
    private UUID clientId;

    @GeneratedValue
    @Column(name = "client_secret", nullable = false, unique = true)
    private UUID clientSecret;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type")
    private AccessType accessType;

    @Column(name = "valid_redirect_uri")
    private String validRedirectUri;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "clients_roles",
            joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "client_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    private Set<Role> roles;

    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Session> sessions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public Client() {
        this.clientId = UUID.randomUUID();
        this.setStatus(Status.ACTIVE);
    }

    public enum AccessType {
        @JsonProperty("confidential")
        CONFIDENTIAL,
        @JsonProperty("public")
        PUBLIC,
        @JsonProperty("bearer_only")
        BEARER_ONLY
    }

}
