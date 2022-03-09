package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidDiscriminatorArgumentException extends ConfigurationException {
  private final String invalidDiscriminator;

  public InvalidDiscriminatorArgumentException(String invalidDiscriminator) {
    super(format("The string '%s' is not a valid discriminator", invalidDiscriminator));
    this.invalidDiscriminator = invalidDiscriminator;
  }

  /**
   * @return the invalidDiscriminator
   */
  public String getInvalidDiscriminator() {
    return invalidDiscriminator;
  }
}
