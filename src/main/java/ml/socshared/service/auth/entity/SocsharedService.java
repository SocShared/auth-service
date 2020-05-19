package ml.socshared.service.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "socshared_service")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "relationClass"})
@ToString
@EqualsAndHashCode(callSuper = false)
public class SocsharedService extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @GeneratedValue
    @Column(name = "client_secret", nullable = false, unique = true)
    private UUID serviceSecret;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "host_url")
    private String hostUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_token_id")
    private ServiceToken serviceToken;

}
