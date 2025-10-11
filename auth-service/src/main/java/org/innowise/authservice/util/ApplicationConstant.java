package org.innowise.authservice.util;

public class ApplicationConstant {


    private ApplicationConstant() {
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    public static final String JWT_INVALID = "JWT processing failed";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
    public static final String ACCESS_DENIED = "Access denied";

    public static final String NOT_FOUND_ERROR_CODE = "NOT_FOUND";
    public static final String VALIDATION_ERROR_CODE = "VALIDATION";
    public static final String CONFLICT_ERROR_CODE = "CONFLICT";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    public static final String ACCESS_DENIED_ERROR_CODE = "ACCESS_DENIED";
    public static final String AUTHENTICATION_FAILED_ERROR_CODE = "AUTHENTICATION_FAILED";
    public static final String JWT_FAILED_ERROR_CODE = "JWT_FAILED";
}
