package ml.socshared.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.entity.base.BaseEntity;

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
    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @GeneratedValue
    @Column(name = "client_secret", nullable = false, unique = true)
    private UUID serviceSecret;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "host_url")
    private String hostUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fromService")
    private Set<ServiceToken> serviceTokens;

}
