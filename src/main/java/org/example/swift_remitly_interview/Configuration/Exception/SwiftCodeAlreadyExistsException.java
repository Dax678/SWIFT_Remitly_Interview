package org.example.swift_remitly_interview.Configuration.Exception;

import java.text.MessageFormat;

public class SwiftCodeAlreadyExistsException extends RuntimeException {
    public SwiftCodeAlreadyExistsException(String message) {
        super(message);
    }

    public SwiftCodeAlreadyExistsException(String message, Object ... args) {
        super(MessageFormat.format(message, args));
    }
}
