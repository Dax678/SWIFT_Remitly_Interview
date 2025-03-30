package org.example.swift_remitly_interview.Configuration.Exception;

import java.text.MessageFormat;

public class SwiftCodeNotFoundException extends RuntimeException {
    public SwiftCodeNotFoundException(String message) {
        super(message);
    }

    public SwiftCodeNotFoundException(String message, Object ... args) {
        super(MessageFormat.format(message, args));
    }
}
