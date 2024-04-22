package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public record DereferencedCommandAndParsedArguments<T>(Command<T> rootCommand,
    List<Discriminator> discriminators, SingleCommand<? extends T> dereferencedCommand,
    List<ParsedArgument> parsedArguments) {

  public DereferencedCommandAndParsedArguments {
    rootCommand = requireNonNull(rootCommand);
    discriminators = unmodifiableList(discriminators);
    dereferencedCommand = requireNonNull(dereferencedCommand);
    parsedArguments = unmodifiableList(parsedArguments);
  }
}
