package org.innowise.userservice.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.innowise.userservice.model.dto.ApiError;
import org.innowise.userservice.model.dto.ValidationError;
import org.innowise.userservice.util.ErrorConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler {
    private static final String NO_ENDPOINT_FOUND = "No endpoint found for %s %s";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        log.error("Validation error occurred: {}", ex.getMessage());

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage(),
                        error.getCode()
                ))
                .toList();

        ApiError apiError = ApiError.withValidationErrors(
                ErrorConstant.VALIDATION_FAILED,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                validationErrors
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex,
                                                          HttpServletRequest request) {
        log.info("Entity not found: {}", ex.getMessage());

        ApiError apiError = ApiError.of(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                ErrorConstant.NOT_FOUND_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(AlreadyExistsException ex,
                                                                   HttpServletRequest request) {
        log.info("Entity already exists: {}", ex.getMessage());

        ApiError apiError = ApiError.of(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI(),
                ErrorConstant.CONFLICT_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(EmptyResourceException.class)
    public ResponseEntity<Object> handleException(EmptyResourceException ex,
                                                  HttpServletRequest request) {
        log.error("Empty resource failed {}: ", request.getRequestURI());

        ApiError apiError = ApiError.of(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                ErrorConstant.NOT_FOUND_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex,
                                                  HttpServletRequest request) {
        log.error("Unexpected error occurred at {}: ", request.getRequestURI(), ex);

        ApiError apiError = ApiError.of(
                ErrorConstant.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                ErrorConstant.INTERNAL_ERROR_CODE
        );

        return ResponseEntity.internalServerError().body(apiError);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex,
                                                                 HttpServletRequest request) {
        log.info("No mapping found for {} {}", ex.getHttpMethod(), ex.getResourcePath());

        ApiError apiError = ApiError.of(
                NO_ENDPOINT_FOUND.formatted(ex.getHttpMethod(), request.getRequestURL()),
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                ErrorConstant.NOT_FOUND_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                     HttpServletRequest request) {
        log.error("Validation error occurred: {}", ex.getMessage());

        ApiError apiError = ApiError.of(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                ErrorConstant.VALIDATION_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex,
                                                              HttpServletRequest request) {
        log.warn("Access denied for request {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.of(
                ErrorConstant.ACCESS_DENIED,
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                ErrorConstant.ACCESS_DENIED_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex,
                                                                     HttpServletRequest request) {
        log.warn("Authorization denied for request {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.of(
                ErrorConstant.ACCESS_DENIED,
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                ErrorConstant.ACCESS_DENIED_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(JwtException ex,
                                                                     HttpServletRequest request) {
        log.warn("JWT validation failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.of(
                ErrorConstant.JWT_VALIDATION_FAILED,
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                ErrorConstant.JWT_ERROR_CODE
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                 HttpServletRequest request) {
        log.warn("Illegal argument error occurred at {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.of(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                ErrorConstant.VALIDATION_FAILED
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

}
