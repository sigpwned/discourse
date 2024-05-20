package com.sigpwned.discourse.core.invocation.phase.eval.exception;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseException;

public class MapFailedEvalPhaseException extends EvalPhaseException {
  private static final long serialVersionUID = 3503422563699036881L;

  private final String propertyName;
  private final String propertyValue;

  public MapFailedEvalPhaseException(String propertyName, String propertyValue, Throwable cause) {
    super("Failed to map property %s value %s".formatted(propertyName, propertyValue), cause);
    this.propertyName = requireNonNull(propertyName);
    this.propertyValue = requireNonNull(propertyValue);
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getPropertyValue() {
    return propertyValue;
  }
}
