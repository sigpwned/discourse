package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class UnexpectedDiscriminatorConfigurationException extends ConfigurationException {
  private final Class<?> rawType;

  public UnexpectedDiscriminatorConfigurationException(Class<?> rawType) {
    super(format("Configuration %s should have no discriminator, but has one", rawType.getName()));
    this.rawType = rawType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }
}
