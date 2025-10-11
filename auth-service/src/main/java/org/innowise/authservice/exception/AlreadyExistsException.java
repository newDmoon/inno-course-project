package org.innowise.authservice.exception;

import java.io.Serial;

public class AlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3001L;
    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
