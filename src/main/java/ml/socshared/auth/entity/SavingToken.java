package ml.socshared.auth.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "service_token")
@ToString
@EqualsAndHashCode(callSuper = false)
public class SavingToken {

    @Id
    @GeneratedValue
    @Column(name = "token_id")
    private UUID toServiceId;

    @Column(name = "token", nullable = false, length = 1000)
    private String token;

}
