package ml.socshared.service.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.entity.base.BaseEntity;
import ml.socshared.service.auth.service.jwt.JwtTokenProvider;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "session")
@ToString
@EqualsAndHashCode(callSuper = false)
public class Session extends BaseEntity {

    @Id
    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "active_session")
    private Boolean activeSession;

    @Column(name = "offline_session")
    private Boolean offlineSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "access_token_id")
    private OAuth2AccessToken accessToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "refresh_token_id")
    private OAuth2RefreshToken refreshToken;

}
