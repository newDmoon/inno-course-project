package org.innowise.userservice.advice;

import org.innowise.userservice.exception.CardNotFoundException;
import org.innowise.userservice.exception.UserAlreadyExistsException;
import org.innowise.userservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .toList();

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            CardNotFoundException.class
    })
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserNotFoundException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
