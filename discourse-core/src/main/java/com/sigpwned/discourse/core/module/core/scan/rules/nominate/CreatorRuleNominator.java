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
package com.sigpwned.discourse.core.module.core.scan.rules.nominate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Reflection;

public class CreatorRuleNominator implements RuleNominator {
  public static final CreatorRuleNominator INSTANCE = new CreatorRuleNominator();

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context) {
    List<CandidateRule> result = new ArrayList<>();

    for (Constructor<?> constructor : clazz.getConstructors()) {
      // If it's not annotated, we don't want it
      DiscourseCreator annotation = constructor.getAnnotation(DiscourseCreator.class);
      if (annotation == null)
        continue;

      // If it's the default constructor, that's already handled
      if (Reflection.hasDefaultConstructorSignature(constructor))
        continue;

      result.add(new CandidateRule(constructor, clazz, List.of(constructor.getAnnotations())));
    }

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Method method : ancestor.getDeclaredMethods()) {
        DiscourseCreator annotation = method.getAnnotation(DiscourseCreator.class);
        if (annotation == null)
          continue;

        if (!Reflection.hasFactoryMethodSignature(method))
          continue;

        if (!Modifier.isPublic(method.getModifiers()))
          continue;

        result.add(new CandidateRule(method, method.getGenericReturnType(),
            List.of(method.getAnnotations())));
      }
    }

    return result;
  }
}