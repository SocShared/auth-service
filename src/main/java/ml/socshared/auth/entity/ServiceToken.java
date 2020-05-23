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
@Table(name = "service_token")
@ToString
@EqualsAndHashCode(callSuper = false)
public class ServiceToken extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "token_id")
    private UUID tokenId;

    @Column(name = "token", nullable = false, length = 1000)
    private String token;

    @Column(name = "token_expire_in", nullable = false)
    private Long tokenExpireIn;

    @Column(name = "to_service_id", nullable = false)
    private UUID toServiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_service_id", referencedColumnName = "service_id")
    private SocsharedService fromService;

}
