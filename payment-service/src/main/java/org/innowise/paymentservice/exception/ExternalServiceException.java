package org.innowise.paymentservice.exception;

import java.io.Serial;

public class ExternalServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 20005L;

    public ExternalServiceException() {
        super();
    }

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
