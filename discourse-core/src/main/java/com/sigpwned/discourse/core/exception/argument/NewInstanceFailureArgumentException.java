package com.sigpwned.discourse.core.exception.argument;

import com.sigpwned.discourse.core.ArgumentException;

public class NewInstanceFailureArgumentException extends ArgumentException {
  public NewInstanceFailureArgumentException(Exception cause) {
    super("Failed to create new configuration instance", cause);
  }
}
