package org.innowise.orderservice.util;

public class ApplicationConstant {
    private ApplicationConstant() {
    }

    public static final String ID = "id";

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String ENTITY_WITH_ID_NOT_FOUND = "Entity with id %d not found";

    public static final String NOT_FOUND_ERROR_CODE = "NOT_FOUND";
    public static final String VALIDATION_ERROR_CODE = "VALIDATION";
    public static final String CONFLICT_ERROR_CODE = "CONFLICT";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
}
