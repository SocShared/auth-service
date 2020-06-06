package ml.socshared.auth.domain.request.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeFlow {
    @JsonProperty("password")
    PASSWORD,
    @JsonProperty("refresh_token")
    REFRESH_TOKEN,
    @JsonProperty("authorization_code")
    AUTHORIZATION_CODE,
    @JsonProperty("client_credentials")
    CLIENT_CREDENTIALS
}
