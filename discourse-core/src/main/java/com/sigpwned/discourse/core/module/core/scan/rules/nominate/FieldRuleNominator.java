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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Reflection;

public class FieldRuleNominator implements RuleNominator {
  public static final FieldRuleNominator INSTANCE = new FieldRuleNominator();

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context) {
    List<CandidateRule> result = new ArrayList<>();

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Field field : ancestor.getDeclaredFields()) {
        if (Modifier.isPublic(field.getModifiers()) && Reflection.isMutableInstanceField(field)) {
          result.add(new CandidateRule(field.getName(), field, field.getGenericType(),
              List.of(field.getAnnotations())));
        }
      }
    }

    return result;
  }
}
