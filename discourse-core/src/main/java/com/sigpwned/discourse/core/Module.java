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

import static java.util.Collections.emptyList;
import java.util.List;
import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.step.postprocess.args.ArgsPostprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.args.ArgsPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.tokens.TokensPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SubCommandScanner;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDefaultValueExtractor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDescriber;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxExampleValueExtractor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxRequiredChecker;

/**
 * Container for registering various components of a Discourse application.
 */
public abstract class Module {

  public void registerSubCommandScanners(Chain<SubCommandScanner> chain) {}

  public void registerSyntaxNominators(Chain<SyntaxNominator> chain) {}

  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {}

  public void registerSyntaxDefaultValueExtractor(Chain<SyntaxDefaultValueExtractor> chain) {}

  public void registerSyntaxExampleValueExtractor(Chain<SyntaxExampleValueExtractor> chain) {}

  public void registerSyntaxDescriber(Chain<SyntaxDescriber> chain) {}
  
  public void registerSyntaxRequiredChecker(Chain<SyntaxRequiredChecker> chain) {}

  public void registerRuleNominators(Chain<RuleNominator> chain) {}

  public void registerRuleDetectors(Chain<RuleDetector> chain) {}

  public void registerNamingSchemes(Chain<NamingScheme> chain) {}

  public void registerRuleEvaluators(Chain<RuleEvaluator> chain) {}

  public void registerValueSinkFactories(Chain<ValueSinkFactory> chain) {}

  public void registerValueDeserializerFactories(Chain<ValueDeserializerFactory<?>> chain) {}

  public void registerCoordinatesPreprocessors(Chain<CoordinatesPreprocessor> chain) {}

  public void registerArgsPreprocessors(Chain<ArgsPreprocessor> chain) {}

  public void registerTokensPreprocessors(Chain<TokensPreprocessor> chain) {}

  public void registerArgsPostprocessors(Chain<ArgsPostprocessor> chain) {}

  public void registerExceptionFormatters(Chain<ExceptionFormatter> chain) {}

  public void registerListeners(Chain<InvocationPipelineListener> chain) {}

  /**
   * <p>
   * A list of modules that this module depends on. This method should be overridden to return a
   * list of the modules that this module depends on. By default, this method returns an empty list.
   * </p>
   * 
   * <p>
   * In the absence of circular dependencies, the runtime ensures that the dependencies are fully
   * registered before this module is registered. This is useful for ensuring the relative order of
   * various processors inside chains.
   * </p>
   * 
   * <p>
   * In the presence of circular dependencies, the runtime will still succeed, but the order of the
   * modules may not be predictable.
   * </p>
   * 
   * @return a list of modules that this module depends on
   */
  public List<Module> getDependencies() {
    return emptyList();
  }
}
