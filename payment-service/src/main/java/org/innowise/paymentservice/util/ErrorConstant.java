package org.innowise.paymentservice.util;

public class ErrorConstant {
    private ErrorConstant() {
    }

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String EMPTY_RESOURCE_FAILED = "Empty resource failed";
    public static final String EXTERNAL_SERVICE_FAILED = "External service unavailable";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String NO_ENDPOINT_FOUND = "No endpoint found for %s %s";

    public static final String EXTERNAL_SERVICE_ERROR_CODE = "EXTERNAL_SERVICE_ERROR";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    public static final String NOT_FOUND_ERROR_CODE = "NOT_FOUND";
}
