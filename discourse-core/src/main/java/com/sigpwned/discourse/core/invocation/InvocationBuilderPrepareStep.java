package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.sigpwned.discourse.core.DeserializedArgument;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.MultiCommandDereference;
import com.sigpwned.discourse.core.PreparedArgument;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.Comparator;
import java.util.List;

public class InvocationBuilderPrepareStep<T> {

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final List<DeserializedArgument> deserializedArguments;

  public InvocationBuilderPrepareStep(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand,
      List<DeserializedArgument> deserializedArguments) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.deserializedArguments = unmodifiableList(deserializedArguments);
  }

  public InvocationBuilderBuildStep<T> prepare(InvocationContext context) {
    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.afterDeserializeBeforePrepare(rootCommand, dereferencedCommands,
          resolvedCommand, deserializedArguments);
    });

    List<PreparedArgument> preparedArguments = doPrepare();

    return new InvocationBuilderBuildStep<>(rootCommand, dereferencedCommands, resolvedCommand,
        preparedArguments);
  }

  protected List<PreparedArgument> doPrepare() {
    return deserializedArguments.stream().collect(
        groupingBy(DeserializedArgument::parameter, collectingAndThen(toList(), arguments -> {
          ConfigurationParameter parameter = arguments.get(0).parameter();
          ValueSink sink = parameter.getSink();
          for (DeserializedArgument argument : arguments) {
            sink.put(argument.argumentValue());
          }
          return new PreparedArgument(parameter.getName(), arguments, sink.get().orElse(null));
        }))).values().stream().sorted(Comparator.comparing(PreparedArgument::name)).toList();
  }
}
