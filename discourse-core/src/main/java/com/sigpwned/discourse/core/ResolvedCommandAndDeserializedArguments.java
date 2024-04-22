package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import java.util.List;

public record ResolvedCommandAndDeserializedArguments<T>(Command<T> rootCommand,
    List<MultiCommandDereference<? extends T>> dereferencedCommands,
    Command<? extends T> resolvedCommand, List<DeserializedArgument> deserializedArguments) {

  public ResolvedCommandAndDeserializedArguments {
    rootCommand = requireNonNull(rootCommand);
    dereferencedCommands = unmodifiableList(dereferencedCommands);
    resolvedCommand = requireNonNull(resolvedCommand);
    deserializedArguments = unmodifiableList(deserializedArguments);
  }
}
