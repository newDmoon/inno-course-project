package org.innowise.orderservice.exception;

import org.innowise.orderservice.util.ApplicationConstant;

import java.io.Serial;

public class FetchException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3003L;

    public FetchException() {
        super(ApplicationConstant.FETCH_FAILED);
    }

    public FetchException(String message) {
        super(message);
    }

    public FetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
