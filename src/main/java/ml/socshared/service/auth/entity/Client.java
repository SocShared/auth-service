package ml.socshared.service.auth.entity;

import lombok.*;
import ml.socshared.service.auth.entity.base.BaseEntity;

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
    @GeneratedValue
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

    @OneToOne(mappedBy = "client")
    private SocsharedService service;

    public enum AccessType {
        CONFIDENTIAL, PUBLIC, BEARER_ONLY
    }

}
