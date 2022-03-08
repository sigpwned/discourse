package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidShortNameConfigurationException extends ConfigurationException {
  private final String shortName;

  public InvalidShortNameConfigurationException(String shortName) {
    super(format("The string '%s' is not a valid short name", shortName));
    this.shortName = shortName;
  }

  /**
   * @return the variableName
   */
  public String getShortName() {
    return shortName;
  }
}
