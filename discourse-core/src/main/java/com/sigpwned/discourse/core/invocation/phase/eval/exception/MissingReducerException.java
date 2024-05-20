package com.sigpwned.discourse.core.invocation.phase.eval.exception;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;

/**
 * Exception thrown when a required mapper is not found for the named property. This is an internal
 * error.
 */
public class MissingReducerException extends InternalDiscourseException {
  private static final long serialVersionUID = 6531719233687080146L;

  private final String propertyName;

  public MissingReducerException(String propertyName) {
    super("No reducer for property " + propertyName);
    this.propertyName = requireNonNull(propertyName);
  }

  public String getPropertyName() {
    return propertyName;
  }
}
