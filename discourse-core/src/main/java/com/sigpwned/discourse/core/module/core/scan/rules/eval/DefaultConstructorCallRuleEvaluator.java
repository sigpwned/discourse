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
package com.sigpwned.discourse.core.module.core.scan.rules.eval;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.exception.internal.IllegalArgumentInternalDiscourseException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.RuleEvaluationFailureScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorCallRuleEvaluator implements RuleEvaluator {
  public static final DefaultConstructorCallRuleEvaluator INSTANCE =
      new DefaultConstructorCallRuleEvaluator();

  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    if (rule.nominated() instanceof Constructor constructor
        && Modifier.isPublic(constructor.getModifiers())
        && Reflection.hasDefaultConstructorSignature(constructor)) {
      // Square deal. This is what we're here for.
    } else {
      // We don't execute this guy.
      return Optional.empty();
    }

    if (rule.antecedents().size() != 0) {
      throw new IllegalArgumentInternalDiscourseException(
          "Default constructor rules must have exactly zero antecedents");
    }

    Object instance;
    try {
      instance = constructor.newInstance();
    } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
      // These are all strange. We run checks so they shouldn't happen. However, clearly it did
      // happen here, so there's a bug in the framework.
      throw new InternalDiscourseException("Failed to instantiate object", e);
    } catch (InvocationTargetException | ExceptionInInitializerError e) {
      // Welp, that's no good. The underlying code threw an exception. That's an application
      // problem.
      // TODO Should we catch ExceptionInInitializerError? It's an error...
      throw new RuleEvaluationFailureScanException(null, rule.humanReadableName(), e);
    }

    return Optional.of(Optional.of(instance));
  }

}
