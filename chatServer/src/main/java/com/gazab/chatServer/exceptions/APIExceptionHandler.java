package com.gazab.chatServer.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class APIExceptionHandler {
    
    @ExceptionHandler(value = UsernameAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<ExceptionResponse>(new ExceptionResponse(ex.getLocalizedMessage(), ex, status, LocalDateTime.now()), status);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<ExceptionResponse>(new ExceptionResponse(ex.getLocalizedMessage(), ex, status, LocalDateTime.now()), status);
    }
}
