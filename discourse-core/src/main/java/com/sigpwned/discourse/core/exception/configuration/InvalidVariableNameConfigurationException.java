package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidVariableNameConfigurationException extends ConfigurationException {
  private static final long serialVersionUID = 7675088423164446574L;

  private final String variableName;

  public InvalidVariableNameConfigurationException(String variableName) {
    super(format("The string '%s' is not a valid variable name", variableName));
    this.variableName = variableName;
  }

  /**
   * @return the variableName
   */
  public String getVariableName() {
    return variableName;
  }
}
