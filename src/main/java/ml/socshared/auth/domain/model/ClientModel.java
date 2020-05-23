package ml.socshared.auth.domain.model;

import ml.socshared.auth.entity.Client;

import java.util.UUID;

public interface ClientModel {
    UUID getClientId();
    UUID getClientSecret();
    String getName();
    Client.AccessType getAccessType();
    String getValidRedirectUri();
}
