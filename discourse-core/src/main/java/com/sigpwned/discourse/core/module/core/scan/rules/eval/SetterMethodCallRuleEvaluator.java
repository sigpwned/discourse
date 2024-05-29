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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.Reflection;

public class SetterMethodCallRuleEvaluator implements RuleEvaluator {
  public static final SetterMethodCallRuleEvaluator INSTANCE = new SetterMethodCallRuleEvaluator();

  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    if (rule.nominated() instanceof Method method && Modifier.isPublic(method.getModifiers())
        && Reflection.hasInstanceSetterSignature(method)) {
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

    List<String> antecedentsList = new ArrayList<>(rule.antecedents());
    antecedentsList
        .sort(Comparator.comparingInt(String::length).thenComparing(Function.identity()));
    String instanceName = antecedentsList.get(0);
    String valueName = antecedentsList.get(1);

    if (!valueName.startsWith(instanceName)) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Cannot assign value to field of instance: " + antecedentsList);
    }

    Object instance = input.get(instanceName);
    Object value = input.get(valueName);

    try {
      method.invoke(instance, value);
    } catch (IllegalArgumentException e) {
      // TODO better exception
      throw e;
    } catch (IllegalAccessException e) {
      // We check public, so this shouldn't happen
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      // Welp, that's no good. The underlying code threw an exception.
      // TODO better exception
      throw new RuntimeException(e);
    }

    return Optional.of(Optional.empty());
  }

}
