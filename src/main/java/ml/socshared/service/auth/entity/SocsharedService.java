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

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "host_url")
    private String hostUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "to_service_from_service",
            joinColumns = @JoinColumn(name = "to_service_id", referencedColumnName = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "from_service_id", referencedColumnName = "service_id")
    )
    private Set<SocsharedService> toServices;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "toServices", fetch = FetchType.LAZY)
    private Set<SocsharedService> fromServices;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;
}
