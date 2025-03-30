package org.example.swift_remitly_interview.Configuration.Exception;

import java.text.MessageFormat;

public class CountryISOCodeNotFoundException extends RuntimeException {
  public CountryISOCodeNotFoundException(String message) {
    super(message);
  }

  public CountryISOCodeNotFoundException(String message, Object ... args) {
    super(MessageFormat.format(message, args));
  }
}
