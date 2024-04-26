package com.sigpwned.discourse.core.model.invocation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.command.MultiCommand;

public record MultiCommandDereference<T>(MultiCommand<T> command, Discriminator discriminator) {

  public MultiCommandDereference {
    command = requireNonNull(command);
    discriminator = requireNonNull(discriminator);
    if (!command.getSubcommands().containsKey(discriminator)) {
      throw new IllegalArgumentException(
          String.format("No subcommand with discriminator %s", discriminator));
    }
  }

}
