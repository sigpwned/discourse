package com.sigpwned.discourse.core.pipeline.invocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactoryChain;
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
import com.sigpwned.discourse.core.syntax.UnixSyntax;

public class InvocationPipelineBuilder {
  private final InvocationContext context;

  public InvocationPipelineBuilder() {
    this.context = new InvocationContext() {
      private final Map<InvocationContext.Key<?>, Object> values = new HashMap<>();

      @Override
      public <T> Optional<? extends T> get(InvocationContext.Key<T> key) {
        Object value = values.get(key);
        if (value == null)
          return Optional.empty();
        return Optional.of(key.getType().cast(value));
      }

      @Override
      public <T> void set(InvocationContext.Key<T> key, T value) {
        values.put(key, value);
      }
    };
    register(context -> {
      // Single values
      context.set(Syntax.class, new UnixSyntax());

      // Chains
      context.set(InvocationPipelineStepBase.INVOCATION_PIPELINE_LISTENER_KEY,
          new InvocationPipelineListenerChain());
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

  public InvocationPipelineBuilder syntax(Syntax syntax) {
    if (syntax == null)
      throw new NullPointerException("syntax");
    return register(context -> {
      context.set(Syntax.class, syntax);
    });
  }

  public InvocationPipelineBuilder register(Module module) {
    if (module == null)
      throw new NullPointerException();
    return register(context -> {
      module.registerListeners(
          context.get(InvocationPipelineStepBase.INVOCATION_PIPELINE_LISTENER_KEY)
              .map(InvocationPipelineListenerChain.class::cast).orElseThrow());
      module.registerArgsPreprocessors(context.get(PreprocessArgsStep.ARGS_PREPROCESSOR_KEY)
          .map(ArgsPreprocessorChain.class::cast).orElseThrow());
      module.registerCoordinatesPreprocessors(
          context.get(PreprocessCoordinatesStep.COORDINATES_PREPROCESSOR_KEY)
              .map(CoordinatesPreprocessorChain.class::cast).orElseThrow());
      module.registerTokensPreprocessors(context.get(PreprocessTokensStep.TOKENS_PREPROCESSOR_KEY)
          .map(TokensPreprocessorChain.class::cast).orElseThrow());
      module.registerNamingSchemes(
          context.get(ScanStep.NAMING_SCHEME_KEY).map(NamingSchemeChain.class::cast).orElseThrow());
      module.registerSyntaxNominators(context.get(ScanStep.SYNTAX_NOMINATOR_KEY)
          .map(SyntaxNominatorChain.class::cast).orElseThrow());
      module.registerSyntaxDetectors(context.get(ScanStep.SYNTAX_DETECTOR_KEY)
          .map(SyntaxDetectorChain.class::cast).orElseThrow());
      module.registerRuleNominators(context.get(ScanStep.RULE_NOMINATOR_KEY)
          .map(RuleNominatorChain.class::cast).orElseThrow());
      module.registerRuleDetectors(
          context.get(ScanStep.RULE_DETECTOR_KEY).map(RuleDetectorChain.class::cast).orElseThrow());
      module.registerRuleEvaluators(context.get(ScanStep.RULE_EVALUATOR_KEY)
          .map(RuleEvaluatorChain.class::cast).orElseThrow());
      module.registerSubCommandScanners(context.get(ScanStep.SUB_COMMAND_SCANNER_KEY)
          .map(SubCommandScannerChain.class::cast).orElseThrow());
      module.registerValueSinkFactories(context.get(PlanStep.VALUE_SINK_FACTORY_KEY)
          .map(ValueSinkFactoryChain.class::cast).orElseThrow());
      module.registerValueDeserializerFactories(context.get(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY)
          .map(ValueDeserializerFactoryChain.class::cast).orElseThrow());
    });
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
