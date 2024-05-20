/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.ArgsPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.parse.preprocess.TokenStreamPreprocessor;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScanner;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleEvaluator;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactory;

/**
 * Container for registering various components of a Discourse application.
 */
public abstract class Module {

  public void registerSubCommandScanners(Chain<SubCommandScanner> chain) {}

  public void registerSyntaxNominators(Chain<SyntaxNominator> chain) {}

  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {}

  public void registerRuleNominators(Chain<RuleNominator> chain) {}

  public void registerRuleDetectors(Chain<RuleDetector> chain) {}

  public void registerNamingSchemes(Chain<NamingScheme> chain) {}

  public void registerRuleEvaluators(Chain<RuleEvaluator> chain) {}

  public void registerValueSinkFactories(Chain<ValueSinkFactory> chain) {}

  public void registerValueDeserializerFactories(Chain<ValueDeserializerFactory<?>> chain) {}

  public void registerCoordinatesPreprocessors(Chain<CoordinatesPreprocessor> chain) {}

  public void registerArgsPreprocessors(Chain<ArgsPreprocessor> chain) {}

  public void registerTokenStreamPreprocessors(Chain<TokenStreamPreprocessor> chain) {}

  public void registerListeners(Chain<InvocationPipelineListener> chain) {}
}
