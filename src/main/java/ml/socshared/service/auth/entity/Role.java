package ml.socshared.service.auth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.entity.base.BaseEntity;
import ml.socshared.service.auth.entity.base.Status;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "role")
@ToString
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<Client> clients;

    public Role() {
        this.setStatus(Status.ACTIVE);
    }
}
