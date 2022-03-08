package com.sigpwned.discourse.core;

public abstract class ArgumentException extends RuntimeException {
  public ArgumentException(String message) {
    super(message);
  }

  public ArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}
