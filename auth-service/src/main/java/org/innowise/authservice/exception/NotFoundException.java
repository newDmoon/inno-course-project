package org.innowise.authservice.exception;

import org.innowise.authservice.util.ApplicationConstant;

import java.io.Serial;

public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2003L;
    public NotFoundException() {
        super(ApplicationConstant.ENTITY_NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
