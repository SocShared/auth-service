package ml.socshared.service.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "oauth2_access_token")
@ToString
@EqualsAndHashCode(callSuper = false)
public class OAuth2AccessToken extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "token_id")
    private UUID tokenId;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "expire_in", nullable = false)
    private String expireIn;

    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @Column(name = "scope", nullable = false)
    private String scope;

    @OneToOne(mappedBy = "accessToken")
    private Session session;
}
