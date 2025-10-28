package org.innowise.orderservice.exception;

import org.innowise.orderservice.util.ApplicationConstant;

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
    public NotFoundException(Long id) {
        super(ApplicationConstant.ENTITY_WITH_ID_NOT_FOUND.formatted(id));
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
