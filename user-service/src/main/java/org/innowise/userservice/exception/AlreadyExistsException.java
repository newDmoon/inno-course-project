package org.innowise.userservice.exception;

import org.innowise.userservice.util.ErrorConstant;

import java.io.Serial;

public class AlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2002L;
    public AlreadyExistsException() {
        super(ErrorConstant.ENTITY_ALREADY_EXISTS);
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
