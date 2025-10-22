package org.innowise.userservice.exception;

import org.innowise.userservice.util.ErrorConstant;

import java.io.Serial;

public class EmptyResourceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2001L;

    public EmptyResourceException() {
        super(ErrorConstant.EMPTY_RESOURCE_FAILED);
    }

    public EmptyResourceException(String message) {
        super(message);
    }

    public EmptyResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
