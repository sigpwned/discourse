package com.sigpwned.discourse.core.pipeline.invocation.command;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.command.model.CommandResolution;
import com.sigpwned.discourse.core.pipeline.invocation.command.step.FinishStep;
import com.sigpwned.discourse.core.pipeline.invocation.command.step.MapStep;
import com.sigpwned.discourse.core.pipeline.invocation.command.step.ReduceStep;
import com.sigpwned.discourse.core.pipeline.invocation.command.step.ResolveStep;
import com.sigpwned.discourse.core.util.MoreCollectors;
import com.sigpwned.discourse.core.util.ValueDeserializers;

public class CommandInvocationPipeline {
  private final ResolveStep resolve;
  private final ArgsInvocationPipeline argsPipeline;
  private final MapStep map;
  private final ReduceStep reduce;
  private final FinishStep finish;

  public CommandInvocationPipeline() {
    this(new ResolveStep(), new ArgsInvocationPipeline(), new MapStep(), new ReduceStep(),
        new FinishStep());

  }

  /* default */ CommandInvocationPipeline(ResolveStep resolve, ArgsInvocationPipeline argsPipeline,
      MapStep map, ReduceStep reduce, FinishStep finish) {
    this.resolve = requireNonNull(resolve);
    this.argsPipeline = requireNonNull(argsPipeline);
    this.map = requireNonNull(map);
    this.reduce = requireNonNull(reduce);
    this.finish = requireNonNull(finish);
  }

  public <T> T execute(Command<T> model, List<String> args, CommandInvocationContext context) {
    return execute(CommandResolver.ofCommand(model), args, context);
  }

  public <T> T execute(CommandResolver<T> resolver, List<String> args,
      CommandInvocationContext context) {
    CommandResolution<? extends T> resolved =
        resolve.resolve(resolver, args, context).orElseThrow(() -> {
          return new IllegalArgumentException("no model");
        });
    Command<? extends T> resolvedCommand = resolved.getModel();
    List<String> resolvedArgs = resolved.getArgs();

    LeafCommand<? extends T> leaf;
    if (resolvedCommand instanceof LeafCommand<? extends T> leafCommand) {
      leaf = leafCommand;
    } else {
      // TODO better exception
      throw new IllegalArgumentException("command cannot be executed");
    }

    Map<String, List<String>> groupedArgs = argsPipeline.execute(resolvedArgs, context);

    Map<String, Function<String, Object>> mappers = leaf.getProperties().stream()
        .map(p -> Map.entry(p.getName(),
            (Function<String, Object>) ValueDeserializers.toMapper(p.getDeserializer())))
        .collect(MoreCollectors.mapFromEntries());
    Map<String, List<Object>> mappedArgs = map.map(mappers, groupedArgs, context);

    Map<String, Function<List<Object>, Object>> reducers = leaf.getProperties().stream()
        .map(p -> Map.entry(p.getName(), (Function<List<Object>, Object>) p.getSink()))
        .collect(MoreCollectors.mapFromEntries());
    Map<String, Object> reducedArgs = reduce.reduce(reducers, mappedArgs, context);

    T instance = finish.finish(leaf.getConstructor(), reducedArgs, context);

    return instance;
  }
}
