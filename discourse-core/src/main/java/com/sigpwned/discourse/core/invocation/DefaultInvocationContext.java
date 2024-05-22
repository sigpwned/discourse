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
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.ArgsPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.ArgsPreprocessorChain;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessorChain;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessorChain;
import com.sigpwned.discourse.core.invocation.phase.resolve.CommandResolver;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingSchemeChain;
import com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScanner;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScannerChain;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.DefaultRulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetectorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleEvaluatorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominatorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetectorChain;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominatorChain;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactoryChain;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactoryChain;

public class DefaultInvocationContext implements InvocationContext {
  private Syntax syntax;
  private CommandResolver commandResolver;
  private SubCommandScannerChain subCommandScanner;
  private SyntaxNominatorChain syntaxNominator;
  private SyntaxDetectorChain syntaxDetector;
  private RuleNominatorChain ruleNominator;
  private RuleDetectorChain ruleDetector;
  private NamingSchemeChain namingScheme;
  private ValueSinkFactoryChain valueSinkFactory;
  private ValueDeserializerFactoryChain valueDeserializerFactory;
  private CoordinatesPreprocessorChain coordinatesPreprocessor;
  private ArgsPreprocessorChain argsPreprocessor;
  private TokenStreamPreprocessorChain tokenStreamPreprocessor;
  private RuleEvaluatorChain ruleEvaluator;

  public DefaultInvocationContext(Syntax syntax, CommandResolver commandResolver,
      SubCommandScannerChain subCommandScanner, SyntaxNominatorChain syntaxNominator,
      SyntaxDetectorChain syntaxDetector, RuleNominatorChain ruleNominator,
      RuleDetectorChain ruleDetector, NamingSchemeChain namingScheme,
      ValueSinkFactoryChain valueSinkFactory,
      ValueDeserializerFactoryChain valueDeserializerFactory,
      CoordinatesPreprocessorChain coordinatesPreprocessor, ArgsPreprocessorChain argsPreprocessor,
      TokenStreamPreprocessorChain tokenStreamPreprocessor, RuleEvaluatorChain ruleEvaluator) {
    this.syntax = requireNonNull(syntax);
    this.commandResolver = requireNonNull(commandResolver);
    this.subCommandScanner = requireNonNull(subCommandScanner);
    this.syntaxNominator = requireNonNull(syntaxNominator);
    this.syntaxDetector = requireNonNull(syntaxDetector);
    this.ruleNominator = requireNonNull(ruleNominator);
    this.ruleDetector = requireNonNull(ruleDetector);
    this.namingScheme = requireNonNull(namingScheme);
    this.valueSinkFactory = requireNonNull(valueSinkFactory);
    this.valueDeserializerFactory = requireNonNull(valueDeserializerFactory);
    this.coordinatesPreprocessor = requireNonNull(coordinatesPreprocessor);
    this.argsPreprocessor = requireNonNull(argsPreprocessor);
    this.tokenStreamPreprocessor = requireNonNull(tokenStreamPreprocessor);
    this.ruleEvaluator = requireNonNull(ruleEvaluator);
  }

  @Override
  public Syntax getSyntax() {
    return syntax;
  }

  @Override
  public SubCommandScanner getSubCommandScanner() {
    return subCommandScanner;
  }

  @Override
  public SyntaxNominator getSyntaxNominator() {
    return syntaxNominator;
  }

  @Override
  public SyntaxDetector getSyntaxDetector() {
    return syntaxDetector;
  }

  @Override
  public RuleNominator getRuleNominator() {
    return ruleNominator;
  }

  @Override
  public RuleDetector getRuleDetector() {
    return ruleDetector;
  }

  @Override
  public NamingScheme getNamingScheme() {
    return namingScheme;
  }

  @Override
  public ValueSinkFactory getValueSinkFactory() {
    return valueSinkFactory;
  }

  @Override
  public ValueDeserializerFactory<?> getValueDeserializerFactory() {
    return valueDeserializerFactory;
  }

  @Override
  public CommandResolver getCommandResolver() {
    return commandResolver;
  }

  @Override
  public CoordinatesPreprocessor getCoordinatesPreprocessor() {
    return coordinatesPreprocessor;
  }

  @Override
  public ArgsPreprocessor getArgsPreprocessor() {
    return argsPreprocessor;
  }

  @Override
  public TokenStreamPreprocessor getTokenStreamPreprocessor() {
    return tokenStreamPreprocessor;
  }

  @Override
  public RulesEngine getRulesEngine() {
    return new DefaultRulesEngine(ruleEvaluator);
  }
}
