package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class NoNameConfigurationException extends ConfigurationException {
  private static final long serialVersionUID = -4668453035389042717L;

  private final String parameterName;

  public NoNameConfigurationException(String parameterName) {
    super(
        format("Configuration parameter %s has too many configuration annotations", parameterName));
    this.parameterName = parameterName;
  }

  /**
   * @return the name
   */
  public String getParameterName() {
    return parameterName;
  }
}
