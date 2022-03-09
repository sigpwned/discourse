package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.Discriminator;

public class DiscriminatorMismatchConfigurationException extends ConfigurationException {
  private final Class<?> rawType;
  private final Discriminator expectedDiscriminator;
  private final Discriminator actualDiscriminator;

  public DiscriminatorMismatchConfigurationException(Class<?> rawType,
      Discriminator expectedDiscriminator, Discriminator actualDiscriminator) {
    super(format("Configuration %s should have discriminator %s, but has discriminator %s",
        rawType.getName(), expectedDiscriminator, actualDiscriminator));
    this.rawType = rawType;
    this.expectedDiscriminator = expectedDiscriminator;
    this.actualDiscriminator = actualDiscriminator;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return rawType;
  }

  /**
   * @return the expectedDiscriminator
   */
  public Discriminator getExpectedDiscriminator() {
    return expectedDiscriminator;
  }

  /**
   * @return the actualDiscriminator
   */
  public Discriminator getActualDiscriminator() {
    return actualDiscriminator;
  }
}
