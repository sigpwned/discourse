package com.sigpwned.discourse.core.exception.argument;

import com.sigpwned.discourse.core.ConfigurationException;

public class NoSubcommandArgumentException extends ConfigurationException {
  public NoSubcommandArgumentException() {
    super("No subcommand given");
  }
}
