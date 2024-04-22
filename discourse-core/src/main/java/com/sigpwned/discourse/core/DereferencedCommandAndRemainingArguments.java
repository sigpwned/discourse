package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public record DereferencedCommandAndRemainingArguments<T>(Command<T> rootCommand, List<Discriminator> discriminators,
    SingleCommand<? extends T> dereferencedCommand, List<String> remainingArgs) {

  public DereferencedCommandAndRemainingArguments {
    rootCommand = requireNonNull(rootCommand);
    discriminators = unmodifiableList(discriminators);
    dereferencedCommand = requireNonNull(dereferencedCommand);
    remainingArgs = unmodifiableList(remainingArgs);
  }
}
