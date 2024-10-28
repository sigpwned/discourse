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
package com.sigpwned.discourse.core.module.core.scan.rules.detect;

import static java.util.Collections.emptySet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.ScanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.RuleDetection;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;

public class CreatorRuleDetector implements RuleDetector {
  public static final CreatorRuleDetector INSTANCE = new CreatorRuleDetector();

  @Override
  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context) {
    if (candidate.nominated() instanceof Constructor<?> constructor
        && constructor.getAnnotation(DiscourseCreator.class) != null
        && Modifier.isPublic(constructor.getModifiers())
        && !Reflection.hasDefaultConstructorSignature(constructor)) {
      NamingScheme naming = context.get(ScanStep.NAMING_SCHEME_KEY).orElseThrow();

      List<String> parameterNames = new ArrayList<>();
      for (int i = 0; i < constructor.getParameterCount(); i++) {
        Parameter parameter = constructor.getParameters()[i];

        String parameterName = null;
        if (parameterName == null) {
          NamedSyntax parameterSyntax = syntax.stream()
              .filter(si -> si.nominated().equals(parameter)).findFirst().orElse(null);
          if (parameterSyntax != null) {
            // TODO Use NamingScheme to name parameter here?
            parameterName = parameterSyntax.name();
          }
        }
        if (parameterName == null) {
          parameterName = naming.name(parameter).orElse(null);
        }
        if (parameterName == null)
          break;

        parameterNames.add(parameterName);
      }

      // TODO Hoo boy... do we really want to make set element order significant?
      // See CreatorCallRuleEvaluator for why we might want to do this.
      if (parameterNames.size() == constructor.getParameterCount()) {
        return Maybe.yes(new RuleDetection(new LinkedHashSet<>(parameterNames), emptySet(), true));
      }
    }

    if (candidate.nominated() instanceof Method method
        && method.getAnnotation(DiscourseCreator.class) != null
        && Modifier.isPublic(method.getModifiers())
        && Reflection.hasFactoryMethodSignature(method)) {
      NamingScheme naming = context.get(ScanStep.NAMING_SCHEME_KEY).orElseThrow();

      List<String> parameterNames = new ArrayList<>();
      for (int i = 0; i < method.getParameterCount(); i++) {
        Parameter parameter = method.getParameters()[i];

        String parameterName = null;
        if (parameterName == null) {
          NamedSyntax parameterSyntax = syntax.stream()
              .filter(si -> si.nominated().equals(parameter)).findFirst().orElse(null);
          if (parameterSyntax != null) {
            // TODO Use NamingScheme to name parameter here?
            parameterName = parameterSyntax.name();
          }
        }
        if (parameterName == null) {
          parameterName = naming.name(parameter).orElse(null);
        }
        if (parameterName == null)
          break;

        parameterNames.add(parameterName);
      }

      if (parameterNames.size() == method.getParameterCount()) {
        return Maybe.yes(new RuleDetection(Set.copyOf(parameterNames), emptySet(), true));
      }
    }

    // Welp, we're done here.
    return Maybe.maybe();
  }
}
