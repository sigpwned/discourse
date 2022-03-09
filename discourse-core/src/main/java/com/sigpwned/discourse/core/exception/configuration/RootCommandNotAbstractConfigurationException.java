package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class RootCommandNotAbstractConfigurationException extends ConfigurationException {
  private final Class<?> commandType;

  public RootCommandNotAbstractConfigurationException(Class<?> commandType) {
    super(format("Configurable %s not abstract", commandType.getName()));
    this.commandType = commandType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return commandType;
  }
}
