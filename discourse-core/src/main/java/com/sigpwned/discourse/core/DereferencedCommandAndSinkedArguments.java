package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public record DereferencedCommandAndSinkedArguments<T>(Command<T> command,
    List<Discriminator> discriminators,
    SingleCommand<? extends T> dereferencedCommand, List<SinkedArgument> sinkedArguments) {

  public DereferencedCommandAndSinkedArguments {
    command = requireNonNull(command);
    discriminators = unmodifiableList(discriminators);
    dereferencedCommand = requireNonNull(dereferencedCommand);
    sinkedArguments = unmodifiableList(sinkedArguments);
  }
}
