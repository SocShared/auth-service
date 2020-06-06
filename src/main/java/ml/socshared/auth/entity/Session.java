package ml.socshared.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "session")
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
    private OAuth2AccessToken accessToken;

    @OneToOne(cascade = CascadeType.ALL)
    private OAuth2RefreshToken refreshToken;

    @Override
    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ", activeSession=" + activeSession +
                ", offlineSession=" + offlineSession +
                ", clientId=" + client.getClientId() +
                ", userId=" + user.getUserId() +
                '}';
    }
}
