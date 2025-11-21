package org.innowise.orderservice.util;

public class ApplicationConstant {
    private ApplicationConstant() {
    }

    public static final String ID = "id";
    public static final String ROLES = "roles";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    public static final String ACCESS_DENIED = "Access denied";
    public static final String JWT_VALIDATION_FAILED = "JWT token validation failed";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String ENTITY_WITH_ID_NOT_FOUND = "Entity with id %d not found";

    public static final String NOT_FOUND_ERROR_CODE = "NOT_FOUND";
    public static final String VALIDATION_ERROR_CODE = "VALIDATION";
    public static final String CONFLICT_ERROR_CODE = "CONFLICT";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    public static final String ACCESS_DENIED_ERROR_CODE = "ACCESS_DENIED";
    public static final String JWT_ERROR_CODE = "JWT_ERROR";

    public static final String TOPIC_CREATE_PAYMENT = "CREATE_PAYMENT";
    public static final String TOPIC_CREATE_PAYMENT_DLQ = "CREATE_PAYMENT_DLQ";
    public static final String TOPIC_CREATE_ORDER = "CREATE_ORDER";
    public static final String TOPIC_CREATE_ORDER_DLQ = "CREATE_ORDER_DLQ";
    public static final int PARTITION_COUNT = 1;
    public static final short REPLICATION_FACTOR = 1;
}
