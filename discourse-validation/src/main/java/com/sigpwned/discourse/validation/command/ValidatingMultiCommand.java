package com.sigpwned.discourse.validation.command;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.validation.ValidatingInvocation;

public class ValidatingMultiCommand<T> extends MultiCommand<T> {
  public ValidatingMultiCommand(String name, String description, String version,
      Map<Discriminator, ConfigurationClass> subcommands) {
    super(name, description, version, subcommands);
  }

  @Override
  protected Invocation<T> newInvocation(ConfigurationClass configurationClass, List<String> args) {
    return new ValidatingInvocation<T>(this, configurationClass, args);
  }
}
