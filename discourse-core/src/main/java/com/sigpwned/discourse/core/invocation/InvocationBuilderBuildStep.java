package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import com.sigpwned.discourse.core.model.argument.PreparedArgument;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;
import java.util.Map;

public class InvocationBuilderBuildStep<T> {

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final List<PreparedArgument> preparedArguments;

  public InvocationBuilderBuildStep(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> preparedArguments) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.preparedArguments = unmodifiableList(preparedArguments);
  }

  public Invocation<T> build(InvocationContext context) {
    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.beforeBuild(rootCommand, dereferencedCommands, resolvedCommand,
          preparedArguments, context);
    });

    T instance = doBuild();

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.afterBuild(rootCommand, dereferencedCommands, resolvedCommand,
          preparedArguments, instance, context);
    });

    return new Invocation<>(rootCommand, dereferencedCommands, resolvedCommand, instance);
  }

  protected T doBuild() {
    Map<String, Object> arguments = preparedArguments.stream()
        .collect(toMap(PreparedArgument::name, PreparedArgument::sinkedArgumentValue));
    return resolvedCommand.getInstanceFactory().createInstance(arguments);
  }
}
