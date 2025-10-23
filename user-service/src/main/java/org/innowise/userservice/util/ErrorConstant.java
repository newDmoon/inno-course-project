package org.innowise.userservice.util;

import org.springframework.stereotype.Component;

@Component
public class ErrorConstant {
    private ErrorConstant() {}

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String ENTITY_WITH_ID_NOT_FOUND = "Entity with id %d not found";
    public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
    public static final String EMPTY_RESOURCE_FAILED = "Empty resource failed";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String JWT_VALIDATION_FAILED = "JWT token validation failed";

    public static final String NOT_FOUND_ERROR_CODE = "NOT_FOUND";
    public static final String VALIDATION_ERROR_CODE = "VALIDATION";
    public static final String CONFLICT_ERROR_CODE = "CONFLICT";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    public static final String ACCESS_DENIED_ERROR_CODE = "ACCESS_DENIED";
    public static final String JWT_ERROR_CODE = "JWT_ERROR";

}
