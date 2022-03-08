package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidPropertyNameConfigurationException extends ConfigurationException {
  private final String propertyName;

  public InvalidPropertyNameConfigurationException(String propertyName) {
    super(format("The string '%s' is not a valid property name", propertyName));
    this.propertyName = propertyName;
  }

  /**
   * @return the propertyName
   */
  public String getPropertyName() {
    return propertyName;
  }
}
