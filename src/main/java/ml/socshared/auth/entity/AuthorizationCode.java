package ml.socshared.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.entity.base.BaseEntity;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "authorization_code")
@EqualsAndHashCode(callSuper = false)
public class AuthorizationCode extends BaseEntity {

    @Id
    @Column(name = "code", nullable = false)
    private String generatingLink;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "client_id", nullable = false, unique = true)
    private UUID clientId;

    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @Column(name = "expire_in")
    private LocalDateTime expireIn;

}
