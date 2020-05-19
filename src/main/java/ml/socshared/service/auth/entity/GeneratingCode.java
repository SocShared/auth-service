package ml.socshared.service.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.entity.base.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "generating_code")
@ToString
@EqualsAndHashCode(callSuper = false)
public class GeneratingCode extends BaseEntity {

    @Id
    @Column(name = "generating_link")
    private String generatingLink;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "expire_in")
    private LocalDateTime expireIn;

    public enum Type {
        EMAIL_CONFIRMATION,
        RESET_PASSWORD
    }
}
