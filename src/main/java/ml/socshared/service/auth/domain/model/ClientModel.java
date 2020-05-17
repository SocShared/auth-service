package ml.socshared.service.auth.domain.model;

import ml.socshared.service.auth.entity.Client;

import java.util.UUID;

public interface ClientModel {
    UUID getClientId();
    UUID getClientSecret();
    String getName();
    Client.AccessType getAccessType();
    String getValidRedirect();
}
