package ml.socshared.service.auth.entity;

import lombok.*;
import ml.socshared.service.auth.entity.base.BaseEntity;
import ml.socshared.service.auth.entity.base.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "user_details")
@ToString
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean emailVerified;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "password", length = 500)
    private String password;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @Column(name = "is_reset_password", nullable = false)
    private Boolean resetPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Session> sessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Client> clients;

    public User() {
        this.emailVerified = false;
        this.accountNonLocked = true;
        this.resetPassword = false;
        this.setStatus(Status.ACTIVE);
    }

    public List<String> getRoleNames() {
        List<String> result = new LinkedList<>();

        roles.forEach(role -> result.add(role.getName()));

        return result;
    }
}
