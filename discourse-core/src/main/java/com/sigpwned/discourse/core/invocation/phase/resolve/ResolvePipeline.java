package com.sigpwned.discourse.core.invocation.phase.resolve;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.ResolvedCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.ArrayList;
import java.util.List;

public class ResolvePipeline {

  private final ResolvePhase resolvePhase;
  private final ResolvePipelineListenerChain listener;

  public ResolvePipeline(ResolvePhase resolvePhase, ResolvePipelineListenerChain listener) {
    this.resolvePhase = requireNonNull(resolvePhase);
    this.listener = requireNonNull(listener);
  }

  public <T> ResolvedCommand<? extends T> execute(RootCommand<T> rootCommand, List<String> args) {

    ResolvedCommand<? extends T> resolvedCommand = resolvePhase(rootCommand, args);

    return resolvedCommand;
  }


  protected <T> ResolvedCommand<? extends T> resolvePhase(RootCommand<T> rootCommand,
      List<String> args) {

    getListener().beforeResolveCommand(rootCommand, args);

    ResolvedCommand<? extends T> resolvedCommand = getResolvePhase().resolveCommand(rootCommand,
        args);

    Command<? extends T> command = resolvedCommand.command();
    List<CommandDereference<? extends T>> dereferences = new ArrayList(
        resolvedCommand.discriminators());
    List<String> remainingArgs = new ArrayList<>(resolvedCommand.remainingArgs());

    getListener().afterResolveCommand(rootCommand, args, (Command) command, (List) dereferences,
        remainingArgs);

    // If command is of type `T`, then we know the dereferences are of type `? super T`, since
    // a subcommand has to be a subtype of the parent. The compiler, however, does not know this,
    // so we use a raw type.

    return new ResolvedCommand(command, dereferences, remainingArgs);
  }

  protected ResolvePhase getResolvePhase() {
    return resolvePhase;
  }

  protected ResolvePipelineListenerChain getListener() {
    return listener;
  }
}
