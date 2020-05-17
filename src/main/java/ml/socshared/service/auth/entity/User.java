package ml.socshared.service.auth.entity;

import lombok.*;
import ml.socshared.service.auth.entity.base.BaseEntity;

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

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Session> sessions;

    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired;
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;
    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired;
    @Column(name = "last_password_reset_date")
    private Date lastPasswordResetDate;

    public User() {
        this.emailVerified = false;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
    }

    public List<String> getRoleNames() {
        List<String> result = new LinkedList<>();

        roles.forEach(role -> result.add(role.getName()));

        return result;
    }
}
