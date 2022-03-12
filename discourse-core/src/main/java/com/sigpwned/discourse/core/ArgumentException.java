package com.sigpwned.discourse.core;

public abstract class ArgumentException extends RuntimeException {
  protected ArgumentException(String message) {
    super(message);
  }

  protected ArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}
