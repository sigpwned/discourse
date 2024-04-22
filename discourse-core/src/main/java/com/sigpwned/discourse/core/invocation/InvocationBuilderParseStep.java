package com.sigpwned.discourse.core.invocation;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ArgumentsParser;
import com.sigpwned.discourse.core.ArgumentsParser.Handler;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.MultiCommandDereference;
import com.sigpwned.discourse.core.ParsedArgument;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import java.util.ArrayList;
import java.util.List;

public class InvocationBuilderParseStep<T> {

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final List<String> remainingArguments;

  public InvocationBuilderParseStep(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArguments) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.remainingArguments = unmodifiableList(remainingArguments);
  }

  public InvocationBuilderDeserializeStep<T> parse(InvocationContext context) {
    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.afterResolveBeforeParse(rootCommand, dereferencedCommands, resolvedCommand,
          remainingArguments);
    });

    List<ParsedArgument> parsedArguments = doParse();

    return new InvocationBuilderDeserializeStep<>(rootCommand, dereferencedCommands,
        resolvedCommand, parsedArguments);
  }

  protected List<ParsedArgument> doParse() {
    final List<ParsedArgument> parsedArguments = new ArrayList<>();

    new ArgumentsParser(resolvedCommand, new Handler() {
      @Override
      public void flag(NameCoordinate coordinate, FlagConfigurationParameter property) {
        parsedArguments.add(new ParsedArgument(coordinate, property, "true"));
      }

      @Override
      public void option(NameCoordinate coordinate, OptionConfigurationParameter property,
          String value) {
        parsedArguments.add(new ParsedArgument(coordinate, property, value));
      }

      @Override
      public void positional(PositionCoordinate coordinate,
          PositionalConfigurationParameter property, String value) {
        parsedArguments.add(new ParsedArgument(coordinate, property, value));
      }
    }).parse(remainingArguments);

    return parsedArguments;
  }
}
