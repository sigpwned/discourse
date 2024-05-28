package com.sigpwned.discourse.core.pipeline.invocation;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.ParentCommand;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.command.SuperCommand;
import com.sigpwned.discourse.core.module.CoreModule;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.pipeline.invocation.step.AttributeStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.FinishStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.GroupStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.MapStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ParseStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PreprocessArgsStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PreprocessCoordinatesStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PreprocessTokensStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ReduceStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ResolveStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.ScanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.TokenizeStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.CommandResolver;
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

      return Optional.of(new CommandResolution<>(new ResolvedCommand<>(root.getName().orElse(null),
          root.getVersion().orElse(null), parents, command), args));
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
  private final FinishStep finish;
  private final InvocationContext context;

  public InvocationPipeline(ScanStep scan, ResolveStep resolve, PlanStep plan,
      PreprocessCoordinatesStep preprocessCoordinates, PreprocessArgsStep preprocessArgs,
      TokenizeStep tokenize, PreprocessTokensStep preprocessTokens, ParseStep parse,
      AttributeStep attribute, GroupStep group, MapStep map, ReduceStep reduce, FinishStep finish,
      InvocationContext context) {
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
    this.finish = requireNonNull(finish);
    this.context = requireNonNull(context);
  }

  public <T> T invoke(Class<T> clazz, List<String> args) {
    T instance;
    try {
      getListener(context).beforePipeline();
      instance = doInvoke(clazz, args);
      getListener(context).afterPipeline(instance);
    } catch (Exception e) {
      getListener(context).catchPipeline(e);
      throw e;
    } finally {
      getListener(context).finallyPipeline();
    }
    return instance;
  }

  protected <T> T doInvoke(Class<T> clazz, List<String> args) {
    RootCommand<T> rootCommand = scan.scan(clazz, context);

    context.set(ResolveStep.COMMAND_RESOLVER_KEY, new RootCommandResolver<>(rootCommand));

    CommandResolution<? extends T> commandResolution =
        resolve.<T>resolve(args, context).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("could not resolve command");
        });
    ResolvedCommand<? extends T> resolvedCommand = commandResolution.getCommand();
    List<String> resolvedArgs = commandResolution.getArgs();

    PlannedCommand<? extends T> plannedCommand = plan.plan(resolvedCommand, context);

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

    T instance = finish.finish(plannedCommand.getConstructor(), reducedArgs, context);

    return instance;
  }

  protected InvocationPipelineListener getListener(InvocationContext context) {
    return context.get(InvocationPipelineStepBase.INVOCATION_PIPELINE_LISTENER_KEY)
        .orElseThrow(() -> {
          // TODO better exception
          return new IllegalStateException("no listener");
        });
  }
}
