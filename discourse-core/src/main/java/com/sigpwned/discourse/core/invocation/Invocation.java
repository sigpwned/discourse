package com.sigpwned.discourse.core.invocation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import java.util.List;

public class Invocation<T> {

  private final RootCommand<? super T> rootCommand;
  private final List<CommandDereference<? super T>> commandDereferences;
  private final Command<T> resolvedCommand;
  private final List<String> resolvedArgs;
  private final T instance;

  public Invocation(RootCommand<? super T> rootCommand,
      List<CommandDereference<? super T>> commandDereferences, Command<T> resolvedCommand,
      List<String> resolvedArgs, T instance) {
    this.rootCommand = requireNonNull(rootCommand);
    this.commandDereferences = requireNonNull(commandDereferences);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.resolvedArgs = requireNonNull(resolvedArgs);
    this.instance = requireNonNull(instance);
  }

  public RootCommand<? super T> getRootCommand() {
    return rootCommand;
  }

  public List<CommandDereference<? super T>> getCommandDereferences() {
    return commandDereferences;
  }

  public Command<T> getResolvedCommand() {
    return resolvedCommand;
  }

  public List<String> getResolvedArgs() {
    return resolvedArgs;
  }

  public T getInstance() {
    return instance;
  }
}
