package com.sigpwned.discourse.core.invocation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import com.sigpwned.discourse.core.invocation.model.command.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.ResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.ScanPhase;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandResolution;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvocationPipeline {

  private final ScanPhase scanPhase;
  private final ResolvePhase resolvePhase;
  private final ParsePhase parsePhase;
  private final EvalPhase evalPhase;
  private final FactoryPhase factoryPhase;
  private final InvocationPipelineListener listener;

  public InvocationPipeline(ScanPhase scanPhase, ResolvePhase resolvePhase, ParsePhase parsePhase,
      EvalPhase evalPhase, FactoryPhase factoryPhase, InvocationPipelineListener listener) {
    this.scanPhase = requireNonNull(scanPhase);
    this.resolvePhase = requireNonNull(resolvePhase);
    this.parsePhase = requireNonNull(parsePhase);
    this.evalPhase = requireNonNull(evalPhase);
    this.factoryPhase = requireNonNull(factoryPhase);
    this.listener = requireNonNull(listener);
  }

  public <T> Invocation<? extends T> execute(Class<T> clazz, List<String> args) {
    RootCommand<T> rootCommand = scanPhase(clazz);

    CommandResolution<? extends T> resolution = resolvePhase(rootCommand, args);

    List<Map.Entry<String, String>> parsedArgs = parsePhase(resolution.resolvedCommand(),
        resolution.remainingArgs());

    Map<String, Object> initialState = evalPhase(resolution.resolvedCommand(), parsedArgs);

    T instance = factoryPhase(resolution.resolvedCommand(), initialState);

    return new Invocation(rootCommand, resolution.dereferences(), resolution.resolvedCommand(),
        resolution.remainingArgs(), instance);
  }

  protected <T> RootCommand<T> scanPhase(Class<T> clazz) {
    getListener().beforeScan(clazz);

    RootCommand<T> rootCommand = getScanPhase().scan(clazz);

    getListener().afterScan(clazz, rootCommand);

    return rootCommand;
  }

  protected <T> CommandResolution<? extends T> resolvePhase(RootCommand<T> rootCommand,
      List<String> args) {
    getListener().beforeResolve(rootCommand, args);

    CommandResolution<? extends T> resolution = getResolvePhase().resolveCommand(rootCommand, args);

    Command<? extends T> command = resolution.resolvedCommand();
    List<CommandDereference<? extends T>> dereferences = new ArrayList(resolution.dereferences());
    List<String> remainingArgs = new ArrayList<>(resolution.remainingArgs());

    getListener().afterResolve(rootCommand, args, command, dereferences, remainingArgs);

    return resolution;
  }

  protected <T> List<Map.Entry<String, String>> parsePhase(Command<T> command, List<String> args) {
    getListener().beforeParse(command, args);

    List<Map.Entry<String, String>> parsedArgs = getParsePhase().parse(command, args);

    getListener().afterParse(command, args, parsedArgs);

    return parsedArgs;
  }

  protected <T> Map<String, Object> evalPhase(Command<T> command,
      List<Map.Entry<String, String>> parsedArgs) {
    getListener().beforeEval(command, parsedArgs);

    Map<String, Object> state = getEvalPhase().eval(command, parsedArgs);

    getListener().afterEval(command, parsedArgs, state);

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

  protected EvalPhase getEvalPhase() {
    return evalPhase;
  }

  protected FactoryPhase getFactoryPhase() {
    return factoryPhase;
  }

  protected InvocationPipelineListener getListener() {
    return listener;
  }
}
