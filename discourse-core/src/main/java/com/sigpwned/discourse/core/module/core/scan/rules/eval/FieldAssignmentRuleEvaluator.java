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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.Reflection;

public class FieldAssignmentRuleEvaluator implements RuleEvaluator {
  public static final FieldAssignmentRuleEvaluator INSTANCE = new FieldAssignmentRuleEvaluator();

  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    if (rule.nominated() instanceof Field field && Modifier.isPublic(field.getModifiers())
        && Reflection.isMutableInstanceField(field)) {
      // Square deal. This is what we're here for.
    } else {
      // We don't execute this guy.
      return Optional.empty();
    }

    if (rule.antecedents().size() != 2) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Field assignment rules must have exactly two antecedents");
    }

    if (!rule.antecedents().contains("")) {
      // TODO better exception
      throw new IllegalArgumentException(
          "First antecedent of a field assignment rule must be 'instance'");
    }

    String valueName =
        rule.antecedents().stream().filter(s -> !s.equals("")).findFirst().orElseThrow();

    Object instance = input.get("");
    Object value = input.get(valueName);

    try {
      field.set(instance, value);
    } catch (IllegalArgumentException e) {
      // TODO better exception
      throw e;
    } catch (IllegalAccessException e) {
      // We check public, so this shouldn't happen
      throw new AssertionError(e);
    }

    return Optional.of(Optional.empty());
  }

}
