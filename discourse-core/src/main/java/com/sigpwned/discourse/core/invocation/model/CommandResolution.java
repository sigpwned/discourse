package com.sigpwned.discourse.core.invocation.model;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.command.SubCommand;
import java.util.List;

public record CommandResolution<T>(RootCommand<? super T> rootCommand, Command<T> resolvedCommand,
    List<CommandDereference<? super T>> dereferences, List<String> remainingArgs) {

  public CommandResolution {
    rootCommand = requireNonNull(rootCommand);
    resolvedCommand = requireNonNull(resolvedCommand);
    dereferences = unmodifiableList(dereferences);
    remainingArgs = unmodifiableList(remainingArgs);

    Command<?> command = rootCommand;
    for (CommandDereference<?> dereference : dereferences) {
      SubCommand<?> subcommand = command.getSubcommands().get(dereference.discriminator());
      if (subcommand == null) {
        throw new IllegalArgumentException("Invalid dereference: " + dereference);
      }
      if (subcommand != dereference.command()) {
        throw new IllegalArgumentException("Invalid dereference: " + dereference);
      }
      command = subcommand;
    }
    if (command != resolvedCommand) {
      throw new IllegalArgumentException("Invalid resolved command: " + resolvedCommand);
    }
  }
}
