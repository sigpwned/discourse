package com.sigpwned.discourse.core.invocation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.ResolvedCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvocationPipeline {

  private final ScanPhase scanPhase;
  private final ResolvePhase resolvePhase;
  private final ParsePhase parsePhase;
  private final FactoryPhase factoryPhase;
  private final InvocationPipelineListener listener;

  public InvocationPipeline(ScanPhase scanPhase, ResolvePhase resolvePhase, ParsePhase parsePhase,
      FactoryPhase factoryPhase, InvocationPipelineListener listener) {
    this.scanPhase = requireNonNull(scanPhase);
    this.resolvePhase = requireNonNull(resolvePhase);
    this.parsePhase = requireNonNull(parsePhase);
    this.factoryPhase = requireNonNull(factoryPhase);
    this.listener = requireNonNull(listener);
  }

  public <T> Invocation<? extends T> execute(Class<T> clazz, List<String> args) {
    RootCommand<T> rootCommand = scanPhase(clazz);

    ResolvedCommand<? extends T> resolvedCommand = resolvePhase(rootCommand, args);

    Map<String, Object> initialState = parsePhase(resolvedCommand.command(),
        resolvedCommand.remainingArgs());

    T instance = factoryPhase(resolvedCommand.command(), initialState);

    return new Invocation(rootCommand, resolvedCommand.discriminators(), resolvedCommand.command(),
        resolvedCommand.remainingArgs(), instance);
  }

  protected <T> RootCommand<T> scanPhase(Class<T> clazz) {
    getListener().beforeScan(clazz);

    RootCommand<T> rootCommand = getScanPhase().scan(clazz);

    getListener().afterScan(clazz, rootCommand);

    return rootCommand;
  }

  protected <T> ResolvedCommand<? extends T> resolvePhase(RootCommand<T> rootCommand,
      List<String> args) {
    getListener().beforeResolve(rootCommand, args);

    ResolvedCommand<? extends T> resolvedCommand = getResolvePhase().resolveCommand(rootCommand,
        args);

    Command<? extends T> command = resolvedCommand.command();
    List<CommandDereference<? extends T>> dereferences = new ArrayList(
        resolvedCommand.discriminators());
    List<String> remainingArgs = new ArrayList<>(resolvedCommand.remainingArgs());

    getListener().afterResolve(rootCommand, args, command, dereferences, remainingArgs);

    return resolvedCommand;
  }

  protected <T> Map<String, Object> parsePhase(Command<T> command, List<String> args) {
    getListener().beforeParse(command, args);

    Map<String, Object> state = getParsePhase().parse(command, args);

    getListener().afterParse(command, args, state);

    return state;
  }

  protected <T> T factoryPhase(Command<T> command, Map<String, Object> state) {
    getListener().beforeFactory(command, state);

    T instance = getFactoryPhase().create(command, state);

    getListener().afterFactory(command, state, instance);

    return instance;
  }

  protected ScanPhase getScanPhase() {
    return scanPhase;
  }

  protected ResolvePhase getResolvePhase() {
    return resolvePhase;
  }

  protected ParsePhase getParsePhase() {
    return parsePhase;
  }

  protected FactoryPhase getFactoryPhase() {
    return factoryPhase;
  }

  protected InvocationPipelineListener getListener() {
    return listener;
  }
}
