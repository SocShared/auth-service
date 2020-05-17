package ml.socshared.service.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "scope")
@ToString
@EqualsAndHashCode(callSuper = false)
public class Scope extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "scope_id")
    private UUID scopeId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

}
