package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class AssignmentFailureArgumentException extends ArgumentException {
  private final String propertyName;
  
  public AssignmentFailureArgumentException(String propertyName, Exception cause) {
    super(format("Failed to assign to property %s", propertyName), cause);
    this.propertyName = propertyName;
  }

  public String getPropertyName() {
    return propertyName;
  }
}
