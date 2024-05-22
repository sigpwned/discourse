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
package com.sigpwned.discourse.core.module.scan.rules.detect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;
import com.sigpwned.discourse.core.util.collectors.Only;

public class SetterMethodRuleDetector implements RuleDetector {
  public static final SetterMethodRuleDetector INSTANCE = new SetterMethodRuleDetector();

  @Override
  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context) {
    if (candidate.nominated() instanceof Method nominated
        && Modifier.isPublic(nominated.getModifiers())
        && Reflection.hasInstanceSetterSignature(nominated)) {
      // Groovy. That's what we're here for.
    } else {
      // Welp, we're done here.
      return Maybe.maybe();
    }

    // We need to find the name of the value to pull from the syntax.
    NamedSyntax candidateSyntax = syntax.stream()
        .filter(si -> Objects.equals(si.nominated(), nominated)).collect(Only.toOnly()).orElse(null,
            () -> new IllegalArgumentException("too many syntax for field " + nominated.getName()));
    if (candidateSyntax == null) {
      // This is fine. Not every field is going to be used.
      return Maybe.maybe();
    }

    // TODO instance constant? how do we do mixins?
    // A setter depends on the containing instance and the value to set.
    Set<String> antecedents = Set.of("", candidateSyntax.name());

    // A setter produces no new values
    boolean hasConsequent = false;

    return Maybe.yes(new RuleDetection(antecedents, hasConsequent));
  }
}
