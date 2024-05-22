/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.invocation;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.ResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.ScanPhase;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.ArgsPreprocessorChain;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessorChain;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessorChain;
import com.sigpwned.discourse.core.invocation.phase.resolve.CommandResolver;
import com.sigpwned.discourse.core.invocation.phase.resolve.DefaultCommandResolver;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingSchemeChain;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScannerChain;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetectorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleEvaluatorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominatorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetectorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominatorChain;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactoryChain;
import com.sigpwned.discourse.core.syntax.UnixSyntax;

public class InvocationPipelineBuilder {

  private Syntax syntax;
  private CommandResolver commandResolver;
  private final SubCommandScannerChain subCommandScannerChain;
  private final SyntaxNominatorChain syntaxNominatorChain;
  private final SyntaxDetectorChain syntaxDetectorChain;
  private final RuleNominatorChain ruleNominatorChain;
  private final RuleDetectorChain ruleDetectorChain;
  private final NamingSchemeChain namingSchemeChain;
  private final RuleEvaluatorChain ruleEvaluatorChain;
  private final ValueSinkFactoryChain valueSinkFactoryChain;
  private final ValueDeserializerFactoryChain valueDeserializerFactoryChain;
  private final CoordinatesPreprocessorChain coordinatesPreprocessorChain;
  private final ArgsPreprocessorChain argsPreprocessorChain;
  private final TokenStreamPreprocessorChain tokenStreamPreprocessorChain;
  private final InvocationPipelineListenerChain listenerChain;

  public InvocationPipelineBuilder() {
    this(UnixSyntax.INSTANCE, DefaultCommandResolver.INSTANCE, new SubCommandScannerChain(),
        new SyntaxNominatorChain(), new SyntaxDetectorChain(), new RuleNominatorChain(),
        new RuleDetectorChain(), new NamingSchemeChain(), new RuleEvaluatorChain(),
        new ValueSinkFactoryChain(), new ValueDeserializerFactoryChain(),
        new CoordinatesPreprocessorChain(), new ArgsPreprocessorChain(),
        new TokenStreamPreprocessorChain(), new InvocationPipelineListenerChain());
  }

  public InvocationPipelineBuilder(Syntax syntax, CommandResolver commandResolver,
      SubCommandScannerChain subCommandScannerChain, SyntaxNominatorChain syntaxNominatorChain,
      SyntaxDetectorChain syntaxDetectorChain, RuleNominatorChain ruleNominatorChain,
      RuleDetectorChain ruleDetectorChain, NamingSchemeChain namingSchemeChain,
      RuleEvaluatorChain ruleEvaluatorChain, ValueSinkFactoryChain valueSinkFactoryChain,
      ValueDeserializerFactoryChain valueDeserializerFactoryChain,
      CoordinatesPreprocessorChain coordinatesPreprocessorChain,
      ArgsPreprocessorChain argsPreprocessorChain,
      TokenStreamPreprocessorChain tokenStreamPreprocessorChain,
      InvocationPipelineListenerChain listenerChain) {
    this.syntax = requireNonNull(syntax);
    this.commandResolver = requireNonNull(commandResolver);
    this.subCommandScannerChain = requireNonNull(subCommandScannerChain);
    this.syntaxNominatorChain = requireNonNull(syntaxNominatorChain);
    this.syntaxDetectorChain = requireNonNull(syntaxDetectorChain);
    this.ruleNominatorChain = requireNonNull(ruleNominatorChain);
    this.ruleDetectorChain = requireNonNull(ruleDetectorChain);
    this.namingSchemeChain = requireNonNull(namingSchemeChain);
    this.ruleEvaluatorChain = requireNonNull(ruleEvaluatorChain);
    this.valueSinkFactoryChain = requireNonNull(valueSinkFactoryChain);
    this.valueDeserializerFactoryChain = requireNonNull(valueDeserializerFactoryChain);
    this.coordinatesPreprocessorChain = requireNonNull(coordinatesPreprocessorChain);
    this.argsPreprocessorChain = requireNonNull(argsPreprocessorChain);
    this.tokenStreamPreprocessorChain = requireNonNull(tokenStreamPreprocessorChain);
    this.listenerChain = requireNonNull(listenerChain);
  }

  public InvocationPipelineBuilder register(Module module) {
    module.registerArgsPreprocessors(argsPreprocessorChain);
    module.registerCoordinatesPreprocessors(coordinatesPreprocessorChain);
    module.registerSubCommandScanners(subCommandScannerChain);
    module.registerSyntaxDetectors(syntaxDetectorChain);
    module.registerSyntaxNominators(syntaxNominatorChain);
    module.registerRuleDetectors(ruleDetectorChain);
    module.registerRuleEvaluators(ruleEvaluatorChain);
    module.registerRuleNominators(ruleNominatorChain);
    module.registerNamingSchemes(namingSchemeChain);
    module.registerTokenStreamPreprocessors(tokenStreamPreprocessorChain);
    module.registerValueDeserializerFactories(valueDeserializerFactoryChain);
    module.registerValueSinkFactories(valueSinkFactoryChain);
    module.registerListeners(listenerChain);
    return this;
  }

  public InvocationPipelineBuilder syntax(Syntax syntax) {
    if (syntax == null)
      throw new NullPointerException();
    this.syntax = syntax;
    return this;
  }

  public InvocationPipelineBuilder commandResolver(CommandResolver commandResolver) {
    if (commandResolver == null)
      throw new NullPointerException();
    this.commandResolver = commandResolver;
    return this;
  }

  public InvocationPipeline build() {
    final ScanPhase scanPhase = new ScanPhase(listenerChain);
    final ResolvePhase resolvePhase = new ResolvePhase(listenerChain);
    final ParsePhase parsePhase = new ParsePhase(listenerChain);
    final EvalPhase evalPhase = new EvalPhase(listenerChain);
    final FactoryPhase factoryPhase = new FactoryPhase(listenerChain);
    final InvocationContext context = new DefaultInvocationContext(syntax, commandResolver,
        subCommandScannerChain, syntaxNominatorChain, syntaxDetectorChain, ruleNominatorChain,
        ruleDetectorChain, namingSchemeChain, valueSinkFactoryChain, valueDeserializerFactoryChain,
        coordinatesPreprocessorChain, argsPreprocessorChain, tokenStreamPreprocessorChain,
        ruleEvaluatorChain);
    return new InvocationPipeline(context, scanPhase, resolvePhase, parsePhase, evalPhase,
        factoryPhase, listenerChain);
  }
}
