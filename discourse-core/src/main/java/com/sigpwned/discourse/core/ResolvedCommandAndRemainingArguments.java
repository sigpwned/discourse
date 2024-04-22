package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public record ResolvedCommandAndRemainingArguments<T>(Command<T> rootCommand,
    List<MultiCommandDereference<? extends T>> dereferencedCommands,
    SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs) {

  public ResolvedCommandAndRemainingArguments {
    rootCommand = requireNonNull(rootCommand);
    dereferencedCommands = unmodifiableList(dereferencedCommands);
    resolvedCommand = requireNonNull(resolvedCommand);
    remainingArgs = unmodifiableList(remainingArgs);
  }
}
