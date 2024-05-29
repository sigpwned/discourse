package com.sigpwned.discourse.core.pipeline.invocation;

import static java.util.stream.Collectors.joining;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.dialect.UnixDialect;
import com.sigpwned.discourse.core.format.ExceptionFormatterChain;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.format.VersionFormatter;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.optional.OptionalInvocationContextProperty;
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
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.args.ArgsPreprocessorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates.CoordinatesPreprocessorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.tokens.TokensPreprocessorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingSchemeChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetectorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluatorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominatorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SubCommandScannerChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetectorChain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominatorChain;

public class InvocationPipelineBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(InvocationPipelineBuilder.class);

  private final InvocationContext context;
  private final Set<Class<? extends Module>> loaded;

  public InvocationPipelineBuilder() {
    this.loaded = new HashSet<>();
    this.context = new InvocationContext() {
      private final Map<InvocationContext.Key<?>, Object> values = new HashMap<>();

      @Override
      public <T> OptionalInvocationContextProperty<T, ? extends T> get(
          InvocationContext.Key<T> key) {
        Object value = values.get(key);
        if (value == null)
          return OptionalInvocationContextProperty.empty(key);
        return OptionalInvocationContextProperty.of(key, key.getType().cast(value));
      }

      @Override
      public <T> void set(InvocationContext.Key<T> key, T value) {
        values.put(key, value);
      }
    };
    register(context -> {
      // Single values
      context.set(Dialect.class, new UnixDialect());

      // TODO help formatter
      // TODO version formatter

      // Chains
      context.set(InvocationPipelineStep.INVOCATION_PIPELINE_LISTENER_KEY,
          new InvocationPipelineListenerChain());
      context.set(InvocationPipelineStep.EXCEPTION_FORMATTER_KEY, new ExceptionFormatterChain());
      context.set(ScanStep.NAMING_SCHEME_KEY, new NamingSchemeChain());
      context.set(ScanStep.SYNTAX_NOMINATOR_KEY, new SyntaxNominatorChain());
      context.set(ScanStep.SYNTAX_DETECTOR_KEY, new SyntaxDetectorChain());
      context.set(ScanStep.RULE_NOMINATOR_KEY, new RuleNominatorChain());
      context.set(ScanStep.RULE_DETECTOR_KEY, new RuleDetectorChain());
      context.set(ScanStep.RULE_EVALUATOR_KEY, new RuleEvaluatorChain());
      context.set(ScanStep.SUB_COMMAND_SCANNER_KEY, new SubCommandScannerChain());
      context.set(PlanStep.VALUE_SINK_FACTORY_KEY, new ValueSinkFactoryChain());
      context.set(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY, new ValueDeserializerFactoryChain());
      context.set(PreprocessCoordinatesStep.COORDINATES_PREPROCESSOR_KEY,
          new CoordinatesPreprocessorChain());
      context.set(PreprocessArgsStep.ARGS_PREPROCESSOR_KEY, new ArgsPreprocessorChain());
      context.set(PreprocessTokensStep.TOKENS_PREPROCESSOR_KEY, new TokensPreprocessorChain());
    });
  }

  public InvocationPipelineBuilder dialect(Dialect dialect) {
    if (dialect == null)
      throw new NullPointerException();
    return register(context -> {
      context.set(Dialect.class, dialect);
    });
  }

  public InvocationPipelineBuilder helpFormatter(HelpFormatter helpFormatter) {
    if (helpFormatter == null)
      throw new NullPointerException();
    return register(context -> {
      context.set(HelpFormatter.class, helpFormatter);
    });
  }

  public InvocationPipelineBuilder versionFormatter(VersionFormatter versionFormatter) {
    if (versionFormatter == null)
      throw new NullPointerException();
    return register(context -> {
      context.set(VersionFormatter.class, versionFormatter);
    });
  }

  public InvocationPipelineBuilder register(Module module) {
    register(new ArrayList<>(), module);
    return this;
  }

  private final AtomicBoolean loggedModuleDependencyCycleWarning = new AtomicBoolean(false);

  private void register(List<Class<? extends Module>> loading, Module module) {
    if (module == null)
      throw new NullPointerException();

    // If we've already loaded this module, then we don't need to load it again. Life is good.
    if (loaded.contains(module.getClass()))
      return;

    // If we're already loading this module, then we're in a circular dependency. That's not a fatal
    // error, but it could affect the relative ordering of module registrants. Print a warning (if
    // we haven't already) and move on.
    if (loading.contains(module.getClass())) {
      if (LOGGER.isDebugEnabled()) {
        if (loggedModuleDependencyCycleWarning.getAndSet(true) == false) {
          int firstIndex = loading.indexOf(module.getClass());
          LOGGER.debug(
              "Found circular dependency in module dependencies [{} -> {}], module registration order may not be deterministic",
              loading.subList(firstIndex, loading.size()).stream().map(Class::getSimpleName)
                  .collect(joining(" -> ")),
              module.getClass().getSimpleName());
        }
      }
      return;
    }

    loading.add(module.getClass());
    try {
      for (Module moduleDependency : module.getDependencies())
        register(loading, moduleDependency);

      register(context -> {
        module
            .registerListeners(context.get(InvocationPipelineStep.INVOCATION_PIPELINE_LISTENER_KEY)
                .map(InvocationPipelineListenerChain.class::cast).orElseThrow());
        module
            .registerExceptionFormatters(context.get(InvocationPipelineStep.EXCEPTION_FORMATTER_KEY)
                .map(ExceptionFormatterChain.class::cast).orElseThrow());
        module.registerArgsPreprocessors(context.get(PreprocessArgsStep.ARGS_PREPROCESSOR_KEY)
            .map(ArgsPreprocessorChain.class::cast).orElseThrow());
        module.registerCoordinatesPreprocessors(
            context.get(PreprocessCoordinatesStep.COORDINATES_PREPROCESSOR_KEY)
                .map(CoordinatesPreprocessorChain.class::cast).orElseThrow());
        module.registerTokensPreprocessors(context.get(PreprocessTokensStep.TOKENS_PREPROCESSOR_KEY)
            .map(TokensPreprocessorChain.class::cast).orElseThrow());
        module.registerNamingSchemes(context.get(ScanStep.NAMING_SCHEME_KEY)
            .map(NamingSchemeChain.class::cast).orElseThrow());
        module.registerSyntaxNominators(context.get(ScanStep.SYNTAX_NOMINATOR_KEY)
            .map(SyntaxNominatorChain.class::cast).orElseThrow());
        module.registerSyntaxDetectors(context.get(ScanStep.SYNTAX_DETECTOR_KEY)
            .map(SyntaxDetectorChain.class::cast).orElseThrow());
        module.registerRuleNominators(context.get(ScanStep.RULE_NOMINATOR_KEY)
            .map(RuleNominatorChain.class::cast).orElseThrow());
        module.registerRuleDetectors(context.get(ScanStep.RULE_DETECTOR_KEY)
            .map(RuleDetectorChain.class::cast).orElseThrow());
        module.registerRuleEvaluators(context.get(ScanStep.RULE_EVALUATOR_KEY)
            .map(RuleEvaluatorChain.class::cast).orElseThrow());
        module.registerSubCommandScanners(context.get(ScanStep.SUB_COMMAND_SCANNER_KEY)
            .map(SubCommandScannerChain.class::cast).orElseThrow());
        module.registerValueSinkFactories(context.get(PlanStep.VALUE_SINK_FACTORY_KEY)
            .map(ValueSinkFactoryChain.class::cast).orElseThrow());
        module
            .registerValueDeserializerFactories(context.get(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY)
                .map(ValueDeserializerFactoryChain.class::cast).orElseThrow());
      });

      loaded.add(module.getClass());
    } finally {
      loading.remove(module.getClass());
    }
  }

  public InvocationPipelineBuilder register(Consumer<InvocationContext> withContext) {
    if (withContext == null)
      throw new NullPointerException();
    withContext.accept(context);
    return this;
  }

  public InvocationPipeline build() {
    return new InvocationPipeline(new ScanStep(), new ResolveStep(), new PlanStep(),
        new PreprocessCoordinatesStep(), new PreprocessArgsStep(), new TokenizeStep(),
        new PreprocessTokensStep(), new ParseStep(), new AttributeStep(), new GroupStep(),
        new MapStep(), new ReduceStep(), new FinishStep(), context);
  }
}
