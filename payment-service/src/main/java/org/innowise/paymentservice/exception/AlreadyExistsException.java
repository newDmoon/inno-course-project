package org.innowise.paymentservice.exception;

import org.innowise.paymentservice.util.ErrorConstant;

public class AlreadyExistsException extends RuntimeException {
    static final long serialVersionUID = 20000234L;

    public AlreadyExistsException() {
        super(ErrorConstant.ALREADY_EXISTS_EXCEPTION);
    }

    public AlreadyExistsException(Long id) {
        super(ErrorConstant.ALREADY_EXISTS_WITH_ID_EXCEPTION.formatted(id));
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
