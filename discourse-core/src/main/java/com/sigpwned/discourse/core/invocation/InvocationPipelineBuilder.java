package com.sigpwned.discourse.core.invocation;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.chain.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.chain.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.ResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.ScanPhase;
import com.sigpwned.discourse.core.invocation.phase.eval.impl.DefaultEvalPhase;
import com.sigpwned.discourse.core.invocation.phase.factory.impl.DefaultFactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.parse.impl.ArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.parse.impl.DefaultParsePhase;
import com.sigpwned.discourse.core.invocation.phase.resolve.impl.CommandResolver;
import com.sigpwned.discourse.core.invocation.phase.resolve.impl.DefaultResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.DefaultScanPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.NamingSchemeChain;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.SubCommandScannerChain;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.DefaultRulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleDetectorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleEvaluatorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RuleNominatorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxDetectorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.SyntaxNominatorChain;
import java.util.function.Supplier;

public class InvocationPipelineBuilder {

  private final SubCommandScannerChain subCommandScannerChain;
  private final SyntaxNominatorChain syntaxNominatorChain;
  private final SyntaxDetectorChain syntaxDetectorChain;
  private final RuleNominatorChain ruleNominatorChain;
  private final RuleDetectorChain ruleDetectorChain;
  private final NamingSchemeChain namingSchemeChain;
  private final Supplier<CommandResolver> commandResolverSupplier;
  private final Supplier<ArgumentsParser> argumentsParserSupplier;
  private final RuleEvaluatorChain ruleEvaluatorChain;
  private final Supplier<RulesEngine> rulesEngineSupplier;
  private final ValueSinkFactoryChain valueSinkFactoryChain;
  private final ValueDeserializerFactoryChain valueDeserializerFactoryChain;
  private final InvocationPipelineListenerChain listenerChain;

  public InvocationPipelineBuilder() {
    this(new SubCommandScannerChain(), new SyntaxNominatorChain(), new SyntaxDetectorChain(),
        new RuleNominatorChain(), new RuleDetectorChain(), new NamingSchemeChain(),
        new RuleEvaluatorChain(), new InvocationPipelineListenerChain());
  }

  public InvocationPipelineBuilder(SubCommandScannerChain subCommandScannerChain,
      SyntaxNominatorChain syntaxNominatorChain, SyntaxDetectorChain syntaxDetectorChain,
      RuleNominatorChain ruleNominatorChain, RuleDetectorChain ruleDetectorChain,
      NamingSchemeChain namingSchemeChain, RuleEvaluatorChain ruleEvaluatorChain,
      InvocationPipelineListenerChain listenerChain) {
    this.subCommandScannerChain = requireNonNull(subCommandScannerChain);
    this.syntaxNominatorChain = requireNonNull(syntaxNominatorChain);
    this.syntaxDetectorChain = requireNonNull(syntaxDetectorChain);
    this.ruleNominatorChain = requireNonNull(ruleNominatorChain);
    this.ruleDetectorChain = requireNonNull(ruleDetectorChain);
    this.namingSchemeChain = requireNonNull(namingSchemeChain);
    this.ruleEvaluatorChain = requireNonNull(ruleEvaluatorChain);
    this.listenerChain = requireNonNull(listenerChain);
  }

  public InvocationPipelineBuilder register(Module module) {
    module.registerSubCommandScanners(getSubCommandScannerChain());
    module.registerSyntaxNominators(getSyntaxNominatorChain());
    module.registerSyntaxDetectors(getSyntaxDetectorChain());
    module.registerRuleNominators(getRuleNominatorChain());
    module.registerRuleDetectors(getRuleDetectorChain());
    module.registerNamingSchemes(getNamingSchemeChain());
    module.registerRuleEvaluators(getRuleEvaluatorChain());
    module.registerValueSinkFactories(getValueSinkFactoryChain());
    module.registerValueDeserializerFactories(getValueDeserializerFactoryChain());
    module.registerListeners(getListenerChain());
    return this;
  }

  public InvocationPipeline build() {
    final ScanPhase scanPhase = new DefaultScanPhase(getSubCommandScannerChain(),
        getSyntaxNominatorChain(), getSyntaxDetectorChain(), getRuleNominatorChain(),
        getRuleDetectorChain(), getNamingSchemeChain(),
        new DefaultRulesEngine(getRuleEvaluatorChain()));

    final ResolvePhase resolvePhase = new DefaultResolvePhase(getCommandResolverSupplier(),
        getListenerChain());

    final ParsePhase parsePhase = new DefaultParsePhase(getArgumentsParserSupplier());

    final EvalPhase evalPhase = new DefaultEvalPhase();

    final FactoryPhase factoryPhase = new DefaultFactoryPhase(getRulesEngineSupplier(),
        getListenerChain());

    return new InvocationPipeline(scanPhase, resolvePhase, parsePhase, evalPhase, factoryPhase,
        getListenerChain());
  }

  private SubCommandScannerChain getSubCommandScannerChain() {
    return subCommandScannerChain;
  }

  private SyntaxNominatorChain getSyntaxNominatorChain() {
    return syntaxNominatorChain;
  }

  private SyntaxDetectorChain getSyntaxDetectorChain() {
    return syntaxDetectorChain;
  }

  private RuleNominatorChain getRuleNominatorChain() {
    return ruleNominatorChain;
  }

  private RuleDetectorChain getRuleDetectorChain() {
    return ruleDetectorChain;
  }

  private NamingSchemeChain getNamingSchemeChain() {
    return namingSchemeChain;
  }

  private Supplier<CommandResolver> getCommandResolverSupplier() {
    return commandResolverSupplier;
  }

  private Supplier<ArgumentsParser> getArgumentsParserSupplier() {
    return argumentsParserSupplier;
  }

  private RuleEvaluatorChain getRuleEvaluatorChain() {
    return ruleEvaluatorChain;
  }

  private Supplier<RulesEngine> getRulesEngineSupplier() {
    return rulesEngineSupplier;
  }

  private ValueSinkFactoryChain getValueSinkFactoryChain() {
    return valueSinkFactoryChain;
  }

  private ValueDeserializerFactoryChain getValueDeserializerFactoryChain() {
    return valueDeserializerFactoryChain;
  }

  private InvocationPipelineListenerChain getListenerChain() {
    return listenerChain;
  }
}
