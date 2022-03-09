package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidDiscriminatorConfigurationException extends ConfigurationException {
  private final Class<?> rawType;
  private final String invalidDiscriminator;

  public InvalidDiscriminatorConfigurationException(Class<?> rawType, String invalidDiscriminator) {
    super(format("Configuration %s has invalid discriminator '%s'", rawType.getName(),
        invalidDiscriminator));
    this.rawType = rawType;
    this.invalidDiscriminator = invalidDiscriminator;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }

  /**
   * @return the invalidDiscriminator
   */
  public String getInvalidDiscriminator() {
    return invalidDiscriminator;
  }
}
