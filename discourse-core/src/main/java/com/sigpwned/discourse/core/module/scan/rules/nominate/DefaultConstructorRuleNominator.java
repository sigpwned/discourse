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
package com.sigpwned.discourse.core.module.scan.rules.nominate;

import static java.util.Collections.emptyList;
import java.lang.reflect.Constructor;
import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorRuleNominator implements RuleNominator {
  public static final DefaultConstructorRuleNominator INSTANCE =
      new DefaultConstructorRuleNominator();

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context) {
    Constructor<?> constructor = null;
    for (Constructor<?> candidate : clazz.getConstructors()) {
      if (Reflection.hasDefaultConstructorSignature(candidate)) {
        constructor = candidate;
        break;
      }
    }
    if (constructor == null)
      return emptyList();
    return List.of(new CandidateRule(constructor, clazz, List.of(constructor.getAnnotations())));
  }
}