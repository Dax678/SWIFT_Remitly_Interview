package org.example.swift_remitly_interview.Configuration.Exception;

import java.text.MessageFormat;

public class SwiftCodeDatabaseException extends RuntimeException {
    public SwiftCodeDatabaseException(String message) {
        super(message);
    }

    public SwiftCodeDatabaseException(String message, Object ... args) {
        super(MessageFormat.format(message, args));
    }
}
