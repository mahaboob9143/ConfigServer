package com.spring.ems.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;


import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("status", status.value()); // Renamed from statusCode
        body.put("path", path);
        return body;
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return new ResponseEntity<>(buildErrorBody(HttpStatus.BAD_REQUEST, message, request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    // Handle malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildErrorBody(
                HttpStatus.BAD_REQUEST, "Malformed JSON or invalid request body", request.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    // Handle custom exceptions
    @ExceptionHandler({
            EventNotFoundException.class,
            UserNotFoundException.class,
            TicketNotFoundException.class,
            FeedbackNotFoundException.class,
            NotificationNotFoundException.class,
            UserAlreadyExistsException.class
    })
    public ResponseEntity<Object> handleCustomException(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = ex instanceof UserAlreadyExistsException ? HttpStatus.CONFLICT : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(buildErrorBody(status, ex.getMessage(), request.getRequestURI()), status);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
