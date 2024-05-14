package com.sigpwned.discourse.core.invocation.phase.resolve.impl;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import com.sigpwned.discourse.core.invocation.model.command.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.ResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandResolution;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultResolvePhase implements ResolvePhase {

  private final DefaultResolvePhaseListener listener;

  public DefaultResolvePhase(DefaultResolvePhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  @Override
  public <T> CommandResolution<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args) {
    CommandResolution<? extends T> resolution = resolveStep(rootCommand, args);

    return resolution;
  }

  protected <T> CommandResolution<? extends T> resolveStep(RootCommand<T> rootCommand,
      List<String> originalArgs) {
    // defensive copy
    originalArgs = new ArrayList<>(originalArgs);

    getListener().beforeResolve(rootCommand, originalArgs);

    Command<? extends T> command = rootCommand;
    List<String> remainingArgs = new ArrayList<>(originalArgs);
    Iterator<String> iterator = remainingArgs.iterator();
    List<CommandDereference<? extends T>> commandDereferences = new ArrayList<>();
    while (iterator.hasNext()) {
      String nextArg = remainingArgs.get(0);

      Command<? extends T> subcommand = command.getSubcommands().get(nextArg);
      if (subcommand == null) {
        break;
      }

      commandDereferences.add(new CommandDereference<>(command, nextArg));

      command = subcommand;

      iterator.remove();
    }

    final Command<? extends T> resolvedCommand = command;

    getListener().afterResolve(rootCommand, unmodifiableList(originalArgs), commandDereferences,
        command, remainingArgs);

    // If resolvedCommand is of type `T`, then we know the dereferences are of type `? super T`, since
    // a subcommand has to be a subtype of the parent. The compiler, however, does not know this,
    // so we use a raw type.
    return new CommandResolution(rootCommand, resolvedCommand, commandDereferences, remainingArgs);
  }

  protected DefaultResolvePhaseListener getListener() {
    return listener;
  }
}
