package com.spring.ems.exception;

public class DuplicateFeedbackException extends RuntimeException {
    public DuplicateFeedbackException(String message) {
        super(message);
    }
}