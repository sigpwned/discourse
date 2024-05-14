package com.sigpwned.discourse.core.invocation.phase.resolve.impl;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import com.sigpwned.discourse.core.invocation.phase.ResolvePhase;
import com.sigpwned.discourse.core.command.resolve.CommandResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DefaultResolvePhase implements ResolvePhase {

  private final Supplier<CommandResolver> commandResolverSupplier;
  private final DefaultResolvePhaseListener listener;

  public DefaultResolvePhase(Supplier<CommandResolver> commandResolverSupplier,
      DefaultResolvePhaseListener listener) {
    this.commandResolverSupplier = requireNonNull(commandResolverSupplier);
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

    CommandResolution<? extends T> resolution = (CommandResolution<? extends T>) getCommandResolverSupplier().get()
        .resolveCommand(rootCommand, originalArgs);

    List<CommandDereference<? extends T>> dereferences = (List) resolution.dereferences();
    Command<? extends T> resolvedCommand = resolution.resolvedCommand();
    List<String> remainingArgs = resolution.remainingArgs();

    getListener().afterResolve(rootCommand, originalArgs, dereferences, resolvedCommand,
        remainingArgs);

    return resolution;
  }

  protected Supplier<CommandResolver> getCommandResolverSupplier() {
    return commandResolverSupplier;
  }

  protected DefaultResolvePhaseListener getListener() {
    return listener;
  }
}
