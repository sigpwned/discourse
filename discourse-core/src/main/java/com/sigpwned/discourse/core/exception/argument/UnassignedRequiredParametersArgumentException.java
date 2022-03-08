package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import com.sigpwned.discourse.core.ArgumentException;

public class UnassignedRequiredParametersArgumentException extends ArgumentException {
  private final Set<String> parameterNames;

  public UnassignedRequiredParametersArgumentException(Set<String> parameterNames) {
    super(format("The following required parameters were not assigned: %s", parameterNames));
    if (parameterNames.isEmpty())
      throw new IllegalArgumentException("parameterNames is empty");
    this.parameterNames = unmodifiableSet(parameterNames);
  }

  /**
   * @return the parameterNames
   */
  public Set<String> getParameterNames() {
    return parameterNames;
  }
}
