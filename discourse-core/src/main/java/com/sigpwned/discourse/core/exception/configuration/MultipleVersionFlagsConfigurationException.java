package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class MultipleVersionFlagsConfigurationException extends ConfigurationException {
  private final Class<?> rawType;

  public MultipleVersionFlagsConfigurationException(Class<?> rawType) {
    super(format("Configuration %s has multiple version flags", rawType.getName()));
    this.rawType = rawType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }
}
