package com.sigpwned.discourse.core.invocation.phase;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import com.sigpwned.discourse.core.invocation.phase.resolve.CommandResolver;
import com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener;

public class ResolvePhase {

  private final Supplier<CommandResolver> commandResolverSupplier;
  private final ResolvePhaseListener listener;

  public ResolvePhase(Supplier<CommandResolver> commandResolverSupplier,
      ResolvePhaseListener listener) {
    this.commandResolverSupplier = requireNonNull(commandResolverSupplier);
    this.listener = requireNonNull(listener);
  }

  public final <T> CommandResolution<? extends T> resolve(RootCommand<T> rootCommand,
      List<String> args) {
    CommandResolution<? extends T> resolution = doResolveStep(rootCommand, args);

    return resolution;
  }

  private <T> CommandResolution<? extends T> doResolveStep(RootCommand<T> rootCommand,
      List<String> originalArgs) {
    CommandResolution<? extends T> resolution;
    try {
      getListener().beforeResolvePhaseResolveStep(rootCommand, originalArgs);

      CommandResolution<? extends T> r = resolveStep(rootCommand, originalArgs);

      List<CommandDereference<? extends T>> dereferences = new ArrayList(r.dereferences());
      Command<? extends T> resolvedCommand = r.resolvedCommand();
      List<String> remainingArgs = new ArrayList<>(r.remainingArgs());

      getListener().afterResolvePhaseResolveStep(rootCommand, originalArgs, dereferences,
          resolvedCommand, originalArgs);

      resolution = new CommandResolution(rootCommand, resolvedCommand, dereferences, remainingArgs);
    } catch (Throwable problem) {
      getListener().catchResolvePhaseResolveStep(problem);
      throw problem;
    } finally {
      getListener().finallyResolvePhaseResolveStep();
    }

    return resolution;
  }

  protected <T> CommandResolution<? extends T> resolveStep(RootCommand<T> rootCommand,
      List<String> originalArgs) {
    CommandResolver commandResolver = getCommandResolverSupplier().get();

    CommandResolution<? extends T> resolution = (CommandResolution<? extends T>) commandResolver
        .resolveCommand(rootCommand, unmodifiableList(originalArgs));

    return resolution;
  }

  protected Supplier<CommandResolver> getCommandResolverSupplier() {
    return commandResolverSupplier;
  }

  private ResolvePhaseListener getListener() {
    return listener;
  }
}
