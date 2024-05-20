package com.sigpwned.discourse.core.invocation.phase.eval.exception;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseException;

public class ReduceFailedEvalPhaseException extends EvalPhaseException {
  private static final long serialVersionUID = -7910177844211108869L;

  private final String propertyName;
  private final List<Object> propertyValues;

  public ReduceFailedEvalPhaseException(String propertyName, List<Object> propertyValues,
      Throwable cause) {
    super("Failed to reduce property %s values %s".formatted(propertyName, propertyValues), cause);
    this.propertyName = requireNonNull(propertyName);
    this.propertyValues = unmodifiableList(propertyValues);
  }

  public String getPropertyName() {
    return propertyName;
  }

  public List<Object> getPropertyValues() {
    return propertyValues;
  }
}
