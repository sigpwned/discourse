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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;
import com.sigpwned.discourse.core.util.collectors.Only;

public class FieldRuleDetector implements RuleDetector {
  public static final FieldRuleDetector INSTANCE = new FieldRuleDetector();

  @Override
  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context) {
    if (candidate.nominated() instanceof Field nominated
        && Modifier.isPublic(nominated.getModifiers())
        && Reflection.isMutableInstanceField(nominated)) {
      // Groovy. That's what we're here for.
    } else {
      // Welp, we're done here.
      return Maybe.maybe();
    }

    NamedSyntax candidateSyntax = syntax.stream()
        .filter(si -> Objects.equals(si.nominated(), nominated)).collect(Only.toOnly()).orElse(null,
            () -> new IllegalArgumentException("too many syntax for field " + nominated.getName()));
    if (candidateSyntax == null) {
      // This is fine. Not every field is going to be used.
      return Maybe.maybe();
    }

    // TODO how should we handle the instance parameter?
    Set<String> antecedents = Set.of("", candidateSyntax.name());

    // A field assignment produces no new values
    boolean hasConsequent = false;

    return Maybe.yes(new RuleDetection(antecedents, hasConsequent));
  }
}
