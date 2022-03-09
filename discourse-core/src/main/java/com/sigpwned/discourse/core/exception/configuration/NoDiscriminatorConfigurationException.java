package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class NoDiscriminatorConfigurationException extends ConfigurationException {
  private final Class<?> rawType;
  
  public NoDiscriminatorConfigurationException(Class<?> rawType) {
    super(format("Configuration %s requires discriminator but has none", rawType.getName()));
    this.rawType = rawType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }
}
