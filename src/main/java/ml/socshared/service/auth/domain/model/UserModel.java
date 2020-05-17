package ml.socshared.service.auth.domain.model;

import java.util.UUID;

public interface UserModel {
    UUID getUserId();
    String getUsername();
    String getEmail();
    String getFirstname();
    String getLastname();
}
