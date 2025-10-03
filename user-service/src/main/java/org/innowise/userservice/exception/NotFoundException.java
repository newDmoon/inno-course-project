package org.innowise.userservice.exception;

import org.innowise.userservice.util.ErrorConstant;

import java.io.Serial;

public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2003L;
    public NotFoundException() {
        super(ErrorConstant.ENTITY_NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
