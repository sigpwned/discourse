package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidLongNameConfigurationException extends ConfigurationException {
  private final String longName;

  public InvalidLongNameConfigurationException(String longName) {
    super(format("The string '%s' is not a valid long name", longName));
    this.longName = longName;
  }

  /**
   * @return the variableName
   */
  public String getLongName() {
    return longName;
  }
}
