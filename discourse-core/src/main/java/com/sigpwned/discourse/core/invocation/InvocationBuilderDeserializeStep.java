package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.DeserializedArgument;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.MultiCommandDereference;
import com.sigpwned.discourse.core.ParsedArgument;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public class InvocationBuilderDeserializeStep<T> {

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final List<ParsedArgument> parsedArguments;

  public InvocationBuilderDeserializeStep(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.parsedArguments = unmodifiableList(parsedArguments);
  }

  public InvocationBuilderPrepareStep<T> deserialize(InvocationContext context) {
    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.afterParseBeforeDeserialize(rootCommand, dereferencedCommands, resolvedCommand,
          parsedArguments);
    });

    List<DeserializedArgument> deserializedArguments = parsedArguments.stream()
        .map(this::doDeserialize).toList();

    return new InvocationBuilderPrepareStep<>(rootCommand, dereferencedCommands, resolvedCommand,
        deserializedArguments);
  }

  /**
   * extension hook
   */
  protected DeserializedArgument doDeserialize(ParsedArgument a) {
    Object deserialized = a.parameter().getDeserializer().deserialize(a.argumentText());
    return new DeserializedArgument(a.coordinate(), a.parameter(), a.argumentText(), deserialized);
  }
}
