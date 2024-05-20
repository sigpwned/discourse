package com.sigpwned.discourse.core.invocation.phase.eval.exception;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;

/**
 * Exception thrown when a required mapper is not found for the named property. This is an internal
 * error.
 */
public class MissingMapperException extends InternalDiscourseException {
  private static final long serialVersionUID = -6929390510894742483L;

  private final String propertyName;

  public MissingMapperException(String propertyName) {
    super("No mapper for property " + propertyName);
    this.propertyName = requireNonNull(propertyName);
  }

  public String getPropertyName() {
    return propertyName;
  }
}
