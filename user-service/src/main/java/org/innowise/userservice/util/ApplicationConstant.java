package org.innowise.userservice.util;

public final class ApplicationConstant {
    private ApplicationConstant() {}

    public static final String INTERNAL_HEADER = "X-INTERNAL-AUTH";
    public static final String ROLE_SERVICE = "ROLE_SERVICE";
    public static final String PRINCIPAL_INTERNAL_SERVICE = "internal-service";
    public static final String ID = "id";
    public static final String IDS = "ids";
    public static final String USERS = "users";
    public static final String CARDS = "cards";
    public static final String ROLES = "roles";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

}
