package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class UnexpectedSubcommandsConfigurationException extends ConfigurationException {
  private final Class<?> rawType;

  public UnexpectedSubcommandsConfigurationException(Class<?> rawType) {
    super(format("Configuration %s should have no subcommands, but has some", rawType.getName()));
    this.rawType = rawType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }
}
