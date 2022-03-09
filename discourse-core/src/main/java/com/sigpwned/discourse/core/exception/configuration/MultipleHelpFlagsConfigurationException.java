package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class MultipleHelpFlagsConfigurationException extends ConfigurationException {
  private final Class<?> rawType;

  public MultipleHelpFlagsConfigurationException(Class<?> rawType) {
    super(format("Configuration %s has multiple help flags", rawType.getName()));
    this.rawType = rawType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }
}
