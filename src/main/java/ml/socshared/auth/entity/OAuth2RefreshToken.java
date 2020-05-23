package ml.socshared.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "oauth2_refresh_token")
@ToString
@EqualsAndHashCode(callSuper = false)
public class OAuth2RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "refresh_token_id")
    private UUID refreshTokenId;

    @Column(name = "refresh_token", nullable = false, length = 1000)
    private String refreshToken;

    @Column(name = "refresh_expires_in", nullable = false)
    private Long refreshExpiresIn;

    @OneToOne(mappedBy = "refreshToken")
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    private Session session;
}
