package com.sigpwned.discourse.core;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.sigpwned.discourse.core.ArgumentsParser.Handler;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.exception.syntax.InsufficientDiscriminatorsSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.InvalidDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

public class Foobarticus {

  public <T> T foo(Class<T> rawType, InvocationContext context, List<String> args) {
    Command<T> command = scan(rawType, context);

    Invocation<? extends T> invocation = invoke(command, context, args);

    return invocation.getConfiguration();
  }

  protected <T> ResolvedCommandAndRemainingArguments<T> dereference(Command<T> command,
      InvocationContext context, List<String> args) {

    args = new ArrayList<>(args);

    Command<? extends T> subcommand = command;
    List<MultiCommandDereference<? extends T>> subcommands = new ArrayList<>();
    while (subcommand instanceof MultiCommand<? extends T> multi) {
      if (args.isEmpty()) {
        throw new InsufficientDiscriminatorsSyntaxException(multi);
      }

      String discriminatorString = args.remove(0);

      Discriminator discriminator;
      try {
        discriminator = Discriminator.fromString(discriminatorString);
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorSyntaxException(multi, discriminatorString);
      }

      subcommands.add(new MultiCommandDereference<>(multi, discriminator));

      Command<? extends T> dereferencedSubcommand = multi.getSubcommands().get(discriminator);
      if (dereferencedSubcommand == null) {
        throw new UnrecognizedDiscriminatorSyntaxException(multi, discriminator);
      }

      subcommand = dereferencedSubcommand;
    }

    SingleCommand<? extends T> single = (SingleCommand<? extends T>) subcommand;

    return new ResolvedCommandAndRemainingArguments<>(command, subcommands, single, args);
  }

  protected <T> ResolvedCommandAndParsedArguments<T> parse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs) {
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
    }).parse(remainingArgs);

    return new ResolvedCommandAndParsedArguments<>(rootCommand, dereferencedCommands,
        resolvedCommand, parsedArguments);
  }

  protected <T> ResolvedCommandAndDeserializedArguments<T> deserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments,
      InvocationContext context) {
    final List<DeserializedArgument> deserializedArguments = parsedArguments.stream()
        .map(parsedArgument -> {
          String argumentText = parsedArgument.argumentText();
          Object argumentValue = parsedArgument.parameter().getDeserializer()
              .deserialize(argumentText);
          return new DeserializedArgument(parsedArgument.coordinate(), parsedArgument.parameter(),
              argumentText, argumentValue);
        }).toList();
    return new ResolvedCommandAndDeserializedArguments<>(rootCommand, dereferencedCommands,
        resolvedCommand, deserializedArguments);
  }

  protected <T> ResolvedCommandAndSinkedArguments<T> sink(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<DeserializedArgument> deserializedArguments,
      InvocationContext context) {
    List<PreparedArgument> sinkedArguments = deserializedArguments.stream().collect(
        groupingBy(DeserializedArgument::parameter, collectingAndThen(toList(), arguments -> {
          ConfigurationParameter parameter = arguments.get(0).parameter();
          ValueSink sink = parameter.getSink();
          for (DeserializedArgument argument : arguments) {
            sink.put(argument.argumentValue());
          }
          return new PreparedArgument(parameter.getName(), arguments, sink.get().orElse(null));
        }))).values().stream().sorted(Comparator.comparing(PreparedArgument::name)).toList();
    return new ResolvedCommandAndSinkedArguments<>(rootCommand, dereferencedCommands,
        resolvedCommand, sinkedArguments);
  }

  protected <T> ResolvedCommandAndSinkedArgumentsAndInstance<T> create(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> discriminators,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      InvocationContext context) {
    Map<String, Object> arguments = sinkedArguments.stream()
        .collect(toMap(PreparedArgument::name, PreparedArgument::sinkedArgumentValue));

    T instance = resolvedCommand.getInstanceFactory().createInstance(arguments);

    return new ResolvedCommandAndSinkedArgumentsAndInstance<>(rootCommand, discriminators,
        resolvedCommand, sinkedArguments, instance);
  }

  protected <T> Command<T> scan(Class<T> rawType, InvocationContext context) {
    final AtomicReference<Command<? extends T>> command = new AtomicReference<>();
    new ConfigurableClassWalker<T>(rawType).walk(new ConfigurableClassWalker.Visitor<T>() {
      private Stack<Map<Discriminator, Command<? extends T>>> subcommandsStack = new Stack<>();

      @Override
      public <M extends T> void enterMultiCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        subcommandsStack.push(new HashMap<>());
      }

      @Override
      public <M extends T> void leaveMultiCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        Map<Discriminator, Command<? extends M>> subcommands = (Map) subcommandsStack.pop();
        MultiCommand<M> multi = new MultiCommand<>(clazz, name, description, "1.0", subcommands);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, multi);
        } else {
          command.set(multi);
        }
      }

      @Override
      public <M extends T> void visitSingleCommandClass(Discriminator discriminator, String name,
          String description, Class<M> clazz) {
        ConfigurableInstanceFactoryProviderChain factoryProviderChain = null;
        ConfigurableInstanceFactory<M> factory = factoryProviderChain.getConfigurationInstanceFactory(
            clazz).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("No factory for " + rawType);
        });

        ConfigurableParameterScannerChain parameterScannerChain = null;
        List<ConfigurationParameter> parameters = parameterScannerChain.scanForParameters(clazz);
        if (parameters.isEmpty()) {
          // TODO Warn
        }

        SingleCommand<M> single = new SingleCommand<>(name, description, "1.0", parameters, null);
        if (discriminator != null) {
          subcommandsStack.peek().put(discriminator, single);
        } else {
          command.set(single);
        }
      }
    });
    return (Command<T>) command.get();
  }

  protected <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args) {
    InvocationStrategy strategy = null;
    return strategy.invoke(command, context, args);
  }
}
