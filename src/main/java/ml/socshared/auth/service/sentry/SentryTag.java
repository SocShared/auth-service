package ml.socshared.auth.service.sentry;

public enum SentryTag {
    REGISTRATION_USER("type", "registration_user"),
    EDIT_USER_DATA("type", "edit_user_data"),
    UPDATE_USER_PASSWORD("type", "update_user_password"),
    DELETE_USER_BY_ID("type", "delete_user_by_id"),
    GET_USER_BY_ID("type", "get_user_by_id"),
    GET_USER_BY_USERNAME("type", "get_user_by_username"),
    GET_USER_BY_EMAIL("type", "get_user_by_email"),
    GET_USERS("type", "get_users"),
    CHECK_USERNAME_AND_PASSWORD("type", "check_username_and_password"),
    ADD_ROLE_FOR_USER("type", "add_role_for_user"),
    REMOVE_ROLE_FOR_USER("type", "remove_role_for_user"),
    RESET_PASSWORD("type", "reset_password"),
    GET_SERVICE_TOKEN("type", "get_service_token_response"),
    GET_TOKEN_BY_USERNAME_AND_PASSWORD("type", "get_token_by_username_and_password"),
    GET_TOKEN_BY_REFRESH_TOKEN("type", "get_token_by_refresh_token"),
    GET_TOKEN_BY_AUTHORIZATION_CODE("type", "get_token_by_authorization_code"),
    GET_TOKEN_BY_CLIENT_CREDENTIALS("type", "get_token_by_client_credentials"),
    ADD_CLIENT("type", "add_client"),
    UPDATE_CLIENT("type", "update_client"),
    GET_CLIENT_BY_USER("type", "get_client_by_user"),
    ONLINE_USERS("type", "online_users"),
    ACTIVE_USERS("type", "active_users"),
    NEW_USERS("type", "new_users"),
    ALL_USERS("type", "all_users");

    SentryTag(String t, String tag) {
        type = t;
        sentryTag = tag;
    }

    public String type() {
        return type;
    }
    public String value() {
        return sentryTag;
    }

    private final String sentryTag;
    private final String type;
    public static final String SERVICE_NAME = "AS";

}
