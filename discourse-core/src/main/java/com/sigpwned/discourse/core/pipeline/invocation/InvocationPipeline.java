package com.sigpwned.discourse.core.pipeline.invocation;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.command.planned.ParentCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.resolved.ResolvedCommand;
import com.sigpwned.discourse.core.command.tree.Command;
import com.sigpwned.discourse.core.command.tree.LeafCommand;
import com.sigpwned.discourse.core.command.tree.RootCommand;
import com.sigpwned.discourse.core.command.tree.SuperCommand;
import com.sigpwned.discourse.core.module.CoreModule;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.pipeline.invocation.step.AttributeStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.FinishStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.GroupStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.MapStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ParseStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PostprocessArgsStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PreprocessArgsStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PreprocessCoordinatesStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PreprocessTokensStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ReduceStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ResolveStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ScanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.TokenizeStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.postprocess.args.ArgsPostprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.postprocess.args.ArgsPostprocessorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.CommandResolver;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.exception.PartialCommandResolutionResolveException;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

public class InvocationPipeline {
  public static InvocationPipelineBuilder builder() {
    return new InvocationPipelineBuilder().register(new CoreModule());
  }

  private static final class RootCommandResolver<T> implements CommandResolver<T> {
    private final RootCommand<T> root;

    private RootCommandResolver(RootCommand<T> root) {
      this.root = requireNonNull(root);
    }

    @Override
    public Optional<CommandResolution<? extends T>> resolveCommand(List<String> args,
        InvocationContext context) {
      List<ParentCommand> parents = new ArrayList<>();
      Command<? extends T> command = root.getRoot();
      Iterator<String> iterator = args.iterator();
      while (iterator.hasNext()) {
        boolean resolved = false;
        if (command instanceof SuperCommand<? extends T> superCommand) {
          String nextArg = iterator.next();
          Command<? extends T> subCommand = superCommand.getSubcommands().get(nextArg);
          if (subCommand != null) {
            parents.add(new ParentCommand(nextArg, command.getDescription().orElse(null)));
            command = subCommand;
            iterator.remove();
            resolved = true;
          }
        }
        if (!resolved) {
          break;
        }
      }

      if (command instanceof LeafCommand<? extends T> leaf)
        return Optional
            .of(new CommandResolution<>(new ResolvedCommand<>(root.getName().orElse(null),
                root.getVersion().orElse(null), parents, leaf), args));
      if (command instanceof SuperCommand<? extends T> supercommand)
        throw new PartialCommandResolutionResolveException(supercommand);

      throw new AssertionError("unrecognized command type " + command);
    }
  }

  private final ScanStep scan;
  private final ResolveStep resolve;
  private final PlanStep plan;
  private final PreprocessCoordinatesStep preprocessCoordinates;
  private final PreprocessArgsStep preprocessArgs;
  private final TokenizeStep tokenize;
  private final PreprocessTokensStep preprocessTokens;
  private final ParseStep parse;
  private final AttributeStep attribute;
  private final GroupStep group;
  private final MapStep map;
  private final ReduceStep reduce;
  private final PostprocessArgsStep postprocessArgs;
  private final FinishStep finish;
  private final InvocationContext context;

  public InvocationPipeline(ScanStep scan, ResolveStep resolve, PlanStep plan,
      PreprocessCoordinatesStep preprocessCoordinates, PreprocessArgsStep preprocessArgs,
      TokenizeStep tokenize, PreprocessTokensStep preprocessTokens, ParseStep parse,
      AttributeStep attribute, GroupStep group, MapStep map, ReduceStep reduce,
      PostprocessArgsStep postprocessProperties, FinishStep finish, InvocationContext context) {
    this.scan = requireNonNull(scan);
    this.resolve = requireNonNull(resolve);
    this.plan = requireNonNull(plan);
    this.preprocessCoordinates = requireNonNull(preprocessCoordinates);
    this.preprocessArgs = requireNonNull(preprocessArgs);
    this.tokenize = requireNonNull(tokenize);
    this.preprocessTokens = requireNonNull(preprocessTokens);
    this.parse = requireNonNull(parse);
    this.attribute = requireNonNull(attribute);
    this.group = requireNonNull(group);
    this.map = requireNonNull(map);
    this.reduce = requireNonNull(reduce);
    this.postprocessArgs = requireNonNull(postprocessProperties);
    this.finish = requireNonNull(finish);
    this.context = requireNonNull(context);
  }

  /**
   * Runs Scan step only.
   * 
   * @param <T>
   * @param clazz
   * @return
   */
  public <T> RootCommand<T> scan(Class<T> clazz) {
    RootCommand<T> root;

    try {
      getListener(context).beforePipeline(context);
      root = scan.scan(clazz, context);
      getListener(context).afterPipeline(context);
    } catch (Exception e) {
      getListener(context).catchPipeline(e, context);
      throw e;
    } finally {
      getListener(context).finallyPipeline(context);
    }

    return root;
  }

  /**
   * Runs steps from Scan to Resolve.
   * 
   * @param <T>
   * @param clazz
   * @return
   */
  public <T> CommandResolution<? extends T> resolve(Class<T> clazz, List<String> args) {
    CommandResolution<? extends T> resolution;

    try {
      getListener(context).beforePipeline(context);
      resolution = doScanToResolve(clazz, args);
      getListener(context).afterPipeline(context);
    } catch (Exception e) {
      getListener(context).catchPipeline(e, context);
      throw e;
    } finally {
      getListener(context).finallyPipeline(context);
    }

    return resolution;
  }

  /**
   * Runs steps from Plan to Finish.
   * 
   * @param <T>
   * @param command
   * @param args
   * @return
   */
  public <T> T invoke(ResolvedCommand<T> command, List<String> args) {
    T instance;
    try {
      getListener(context).beforePipeline(context);
      instance = doPlanToFinish(command, args);
      getListener(context).afterPipeline(context);
    } catch (Exception e) {
      getListener(context).catchPipeline(e, context);
      throw e;
    } finally {
      getListener(context).finallyPipeline(context);
    }
    return instance;
  }

  /**
   * Runs all steps from Scan to Finish.
   * 
   * @param <T>
   * @param clazz
   * @param args
   * @return
   */
  public <T> T invoke(Class<T> clazz, List<String> args) {
    T instance;
    try {
      getListener(context).beforePipeline(context);
      CommandResolution<? extends T> resolution = doScanToResolve(clazz, args);
      instance = doPlanToFinish(resolution.getCommand(), resolution.getArgs());
      getListener(context).afterPipeline(context);
    } catch (Exception e) {
      getListener(context).catchPipeline(e, context);
      throw e;
    } finally {
      getListener(context).finallyPipeline(context);
    }
    return instance;
  }

  protected <T> CommandResolution<? extends T> doScanToResolve(Class<T> clazz, List<String> args) {
    RootCommand<T> rootCommand = scan.scan(clazz, context);

    context.set(ResolveStep.COMMAND_RESOLVER_KEY, new RootCommandResolver<>(rootCommand));

    context.set(InvocationPipelineStep.ROOT_COMMAND_KEY, rootCommand);

    CommandResolution<? extends T> commandResolution = resolve.<T>resolve(args, context);

    return commandResolution;
  }

  protected <T> T doPlanToFinish(ResolvedCommand<? extends T> resolvedCommand,
      List<String> resolvedArgs) {
    PlannedCommand<? extends T> plannedCommand = plan.plan(resolvedCommand, context);

    context.get(PostprocessArgsStep.ARGS_POSTPROCESSOR_KEY).map(ArgsPostprocessorChain.class::cast)
        .orElseThrow().addLast(new ArgsPostprocessor() {
          @Override
          public Map<String, Object> postprocessProperties(Map<String, Object> properties,
              InvocationContext context) {
            properties = new HashMap<>(properties);
            plannedCommand.getReactor().accept(properties);
            return unmodifiableMap(properties);
          }
        });

    Map<Coordinate, String> commandProperties = plannedCommand.getProperties().stream()
        .flatMap(p -> p.getCoordinates().stream().map(c -> Map.entry(c, p)))
        .collect(groupingBy(Map.Entry::getKey, Collectors.collectingAndThen(toList(), xs -> {
          if (xs.size() != 1) {
            // TODO better exception
            throw new IllegalArgumentException("duplicate property for " + xs.get(0).getKey());
          }
          return xs.get(0).getValue().getName();
        })));

    Map<Coordinate, String> preprocessedCoordinates =
        preprocessCoordinates.preprocessCoordinates(commandProperties, context);

    List<String> preprocessedArgs = preprocessArgs.preprocessArgs(resolvedArgs, context);

    List<Token> tokens = tokenize.tokenize(preprocessedArgs, context);

    List<Token> preprocessedTokens = preprocessTokens.preprocessTokens(tokens, context);

    List<Map.Entry<Coordinate, String>> parsedArgs = parse.parse(preprocessedTokens, context);

    List<Map.Entry<String, String>> attributedArgs =
        attribute.attribute(preprocessedCoordinates, parsedArgs, context);

    Map<String, List<String>> groupedArgs = group.group(attributedArgs, context);

    Map<String, Function<String, Object>> mappers = plannedCommand.getProperties().stream()
        .collect(toMap(p -> p.getName(), p -> p.getDeserializer()::deserialize));

    Map<String, List<Object>> mappedArgs = map.map(mappers, groupedArgs, context);

    Map<String, Function<List<Object>, Object>> reducers =
        plannedCommand.getProperties().stream().collect(toMap(p -> p.getName(), p -> {
          final ValueSink sink = p.getSink();
          return xs -> {
            for (Object x : xs) {
              sink.put(x);
            }
            return sink.get().orElseThrow(() -> {
              // TODO better exception
              return new IllegalStateException("no values");
            });
          };
        }));

    Map<String, Object> reducedArgs = reduce.reduce(reducers, mappedArgs, context);

    Map<String, Object> postprocessedArgs = postprocessArgs.postprocessArgs(reducedArgs, context);

    T instance = finish.finish(plannedCommand.getConstructor(), postprocessedArgs, context);

    return instance;
  }

  protected InvocationPipelineListener getListener(InvocationContext context) {
    return context.get(InvocationPipelineStep.INVOCATION_PIPELINE_LISTENER_KEY).orElseThrow();
  }
}
