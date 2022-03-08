package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class NewInstanceFailureArgumentException extends ArgumentException {
  public NewInstanceFailureArgumentException(Exception cause) {
    super(format("Failed to create new configuration instance"), cause);
  }
}
