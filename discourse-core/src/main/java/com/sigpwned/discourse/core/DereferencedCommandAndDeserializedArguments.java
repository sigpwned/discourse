package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import java.util.List;

public record DereferencedCommandAndDeserializedArguments<T>(Command<T> rootCommand,
    List<Discriminator> discriminators, Command<? extends T> dereferencedCommand,
    List<DeserializedArgument> deserializedArguments) {

  public DereferencedCommandAndDeserializedArguments {
    rootCommand = requireNonNull(rootCommand);
    discriminators = unmodifiableList(discriminators);
    dereferencedCommand = requireNonNull(dereferencedCommand);
    deserializedArguments = unmodifiableList(deserializedArguments);
  }
}
