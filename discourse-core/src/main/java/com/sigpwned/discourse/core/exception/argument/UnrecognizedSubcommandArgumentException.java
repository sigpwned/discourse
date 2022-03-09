package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.Discriminator;

public class UnrecognizedSubcommandArgumentException extends ConfigurationException {
  private final Discriminator discriminator;

  public UnrecognizedSubcommandArgumentException(Discriminator discriminator) {
    super(format("There is no subcommand for discriminator '%s'", discriminator));
    this.discriminator = discriminator;
  }

  /**
   * @return the invalidDiscriminator
   */
  public Discriminator getInvalidDiscriminator() {
    return discriminator;
  }
}
