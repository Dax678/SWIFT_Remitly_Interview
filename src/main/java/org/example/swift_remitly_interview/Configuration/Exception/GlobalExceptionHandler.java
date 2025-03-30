package org.example.swift_remitly_interview.Configuration.Exception;

import org.example.swift_remitly_interview.Data.DTO.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            SwiftCodeNotFoundException.class,
            CountryISOCodeNotFoundException.class
    })
    public ResponseEntity<MessageResponse> handleCodeNotFoundException(Exception ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            RuntimeException.class,
            SwiftCodeDatabaseException.class
    })
    public ResponseEntity<MessageResponse> handleRuntimeException(RuntimeException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            SwiftCodeAlreadyExistsException.class
    })
    public ResponseEntity<MessageResponse> handleCodeAlreadyExists(Exception ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<MessageResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(er -> er.getField() + ": " + er.getDefaultMessage())
                .orElse("Validation error");
        MessageResponse messageResponse = new MessageResponse(error);
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }
}
