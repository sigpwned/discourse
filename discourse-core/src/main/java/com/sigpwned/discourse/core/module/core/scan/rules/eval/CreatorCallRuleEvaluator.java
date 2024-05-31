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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.Reflection;

public class CreatorCallRuleEvaluator implements RuleEvaluator {
  public static final CreatorCallRuleEvaluator INSTANCE = new CreatorCallRuleEvaluator();

  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    if (rule.nominated() instanceof Constructor<?> constructor
        && constructor.getAnnotation(DiscourseCreator.class) != null
        && Modifier.isPublic(constructor.getModifiers())
        && !Reflection.hasDefaultConstructorSignature(constructor)) {
      // Square deal. This is what we're here for.

      // TODO How do we know the parameter name order?
      // Do we REALLY want to make antecedent set order significant...?
      Object[] args = rule.antecedents().stream().map(input::get).toArray();

      Object instance;
      try {
        instance = constructor.newInstance(args);
      } catch (IllegalArgumentException e) {
        // TODO better exception
        throw e;
      } catch (IllegalAccessException e) {
        // We check public, so this shouldn't happen
        throw new AssertionError(e);
      } catch (InstantiationException e) {
        // Welp, that's no good. The underlying code threw an exception.
        // TODO better exception
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }

      return Optional.of(Optional.of(instance));
    }

    if (rule.nominated() instanceof Method method
        && method.getAnnotation(DiscourseCreator.class) != null
        && Modifier.isPublic(method.getModifiers())
        && Reflection.hasFactoryMethodSignature(method)) {
      // Square deal. This is what we're here for.

      // TODO How do we know the parameter name order?
      // Do we REALLY want to make antecedent set order significant...?
      Object[] args = rule.antecedents().stream().map(input::get).toArray();

      Object instance;
      try {
        instance = method.invoke(null, args);
      } catch (IllegalAccessException e) {
        // We check public, so this shouldn't happen
        throw new AssertionError(e);
      } catch (InvocationTargetException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }

      return Optional.of(Optional.of(instance));
    }

    return Optional.empty();
  }

}
