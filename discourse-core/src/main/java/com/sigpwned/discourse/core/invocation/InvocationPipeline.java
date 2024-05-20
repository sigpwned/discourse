package com.sigpwned.discourse.core.invocation;

import static com.sigpwned.discourse.core.util.MoreCollectors.entriesToMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandBody;
import com.sigpwned.discourse.core.command.CommandProperty;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.ResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.ScanPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;
import com.sigpwned.discourse.core.module.value.sink.ValueSink;

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

  public final <T> Invocation<? extends T> execute(Class<T> clazz, List<String> args) {
    Invocation<? extends T> invocation;
    try {
      getListener().beforeInvocation(clazz, args);
      invocation = doExecute(clazz, args);
      // TODO should we unpack invocation to make it mutable?
      getListener().afterInvocation(clazz, args, invocation);
    } catch (Throwable problem) {
      getListener().catchInvocation(problem);
      throw problem;
    } finally {
      getListener().finallyInvocation();
    }

    return invocation;
  }


  private <T> Invocation<? extends T> doExecute(Class<T> clazz, List<String> args) {
    RootCommand<T> rootCommand = doScanPhase(clazz);

    CommandResolution<? extends T> resolution = doResolvePhase(rootCommand, args);

    List<Map.Entry<String, String>> parsedArgs =
        doParsePhase(resolution.resolvedCommand(), resolution.remainingArgs());

    Map<String, Object> initialState = doEvalPhase(resolution.resolvedCommand(), parsedArgs);

    T instance = doFactoryPhase(resolution.resolvedCommand(), initialState);

    return new Invocation(rootCommand, resolution.dereferences(), resolution.resolvedCommand(),
        resolution.remainingArgs(), instance);
  }

  private <T> RootCommand<T> doScanPhase(Class<T> clazz) {
    RootCommand<T> rootCommand;
    try {
      getListener().beforeScanPhase(clazz);
      rootCommand = scanPhase(clazz);
      getListener().afterScanPhase(clazz, rootCommand);
    } catch (Throwable problem) {
      getListener().catchScanPhase(problem);
      throw problem;
    } finally {
      getListener().finallyScanPhase();
    }

    return rootCommand;
  }

  protected <T> RootCommand<T> scanPhase(Class<T> clazz) {
    return getScanPhase().scan(clazz);
  }

  private <T> CommandResolution<? extends T> doResolvePhase(RootCommand<T> rootCommand,
      List<String> args) {
    CommandResolution<? extends T> resolution;
    try {
      getListener().beforeResolvePhase(rootCommand, args);

      CommandResolution<? extends T> r = resolvePhase(rootCommand, args);

      List<CommandDereference<? extends T>> dereferences = new ArrayList(r.dereferences());
      Command<? extends T> resolvedCommand = r.resolvedCommand();
      List<String> remainingArgs = new ArrayList<>(r.remainingArgs());

      getListener().afterResolvePhase(rootCommand, args, resolvedCommand, dereferences,
          remainingArgs);

      resolution = new CommandResolution(rootCommand, resolvedCommand, dereferences, remainingArgs);
    } catch (Throwable problem) {
      getListener().catchResolvePhase(problem);
      throw problem;
    } finally {
      getListener().finallyResolvePhase();
    }

    return resolution;
  }

  protected <T> CommandResolution<? extends T> resolvePhase(RootCommand<T> rootCommand,
      List<String> args) {
    return getResolvePhase().resolve(rootCommand, args);
  }

  private <T> List<Map.Entry<String, String>> doParsePhase(Command<T> resolvedCommand,
      List<String> remainingArgs) {
    List<Map.Entry<String, String>> namedArgs;
    try {
      getListener().beforeParsePhase(resolvedCommand, remainingArgs);

      namedArgs = parsePhase(resolvedCommand, remainingArgs);

      getListener().afterParsePhase(resolvedCommand, remainingArgs, namedArgs);
    } catch (Throwable problem) {
      getListener().catchParsePhase(problem);
      throw problem;
    } finally {
      getListener().finallyParsePhase();
    }

    // TODO make deeply immutable
    return unmodifiableList(namedArgs);
  }

  protected <T> List<Map.Entry<String, String>> parsePhase(Command<T> resolvedCommand,
      List<String> remainingArgs) {
    CommandBody<T> body = resolvedCommand.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });

    Map<Coordinate, String> names = body.getProperties().stream()
        .flatMap(p -> p.getCoordinates().stream().map(c -> Map.entry(c, p.getName())))
        .collect(entriesToMap());

    return getParsePhase().parse(names, remainingArgs);
  }

  private <T> Map<String, Object> doEvalPhase(Command<T> command,
      List<Map.Entry<String, String>> parsedArgs) {
    Map<String, Object> initialState;
    try {
      getListener().beforeEvalPhase(command, parsedArgs);

      initialState = evalPhase(command, parsedArgs);

      getListener().afterEvalPhase(command, parsedArgs, initialState);
    } catch (Throwable problem) {
      getListener().catchEvalPhase(problem);
      throw problem;
    } finally {
      getListener().finallyEvalPhase();
    }

    return unmodifiableMap(initialState);
  }

  protected <T> Map<String, Object> evalPhase(Command<T> command,
      List<Map.Entry<String, String>> parsedArgs) {
    CommandBody<T> body = command.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });


    Map<String, Function<String, Object>> mappers = body.getProperties().stream()
        .collect(toMap(CommandProperty::getName, e -> e.getDeserializer()::deserialize));

    Map<String, Function<List<Object>, Object>> reducers =
        body.getProperties().stream().collect(toMap(CommandProperty::getName, e -> xs -> {
          ValueSink sink = e.getSink();
          for (Object x : xs)
            sink.put(x);
          return sink.get();
        }));

    return getEvalPhase().eval(mappers, reducers, parsedArgs);
  }

  private <T> T doFactoryPhase(Command<T> resolvedCommand, Map<String, Object> initialState) {
    T instance;
    try {
      getListener().beforeFactoryPhase(resolvedCommand, initialState);

      instance = factoryPhase(resolvedCommand, initialState);

      getListener().afterFactoryPhase(resolvedCommand, initialState, instance);
    } catch (Throwable problem) {
      getListener().catchFactoryPhase(problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhase();
    }

    return instance;
  }

  protected <T> T factoryPhase(Command<T> resolvedCommand, Map<String, Object> initialState) {
    Class<T> resolvedClazz = resolvedCommand.getClazz();

    CommandBody<T> body = resolvedCommand.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });

    List<NamedRule> rules = body.getRules();

    return resolvedClazz.cast(getFactoryPhase().create(rules, initialState));
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
