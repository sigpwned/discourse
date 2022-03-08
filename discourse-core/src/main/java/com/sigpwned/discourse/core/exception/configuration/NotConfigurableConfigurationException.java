package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class NotConfigurableConfigurationException extends ConfigurationException {
  private final Class<?> rawType;

  public NotConfigurableConfigurationException(Class<?> rawType) {
    super(format("Class %s is not @Configurable", rawType.getName()));
    this.rawType = rawType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }
}
