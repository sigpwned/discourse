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
package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;


public class RulesEngine {
  private final RuleEvaluator evaluator;

  public RulesEngine(RuleEvaluator evaluator) {
    this.evaluator = requireNonNull(evaluator);
  }

  public Map<String, Object> run(Map<String, Object> input, List<NamedRule> rules) {
    input = new HashMap<>(input);

    rules = new ArrayList<>(rules);

    boolean changed;
    do {
      changed = false;

      Iterator<NamedRule> iterator = rules.iterator();
      while (iterator.hasNext()) {
        NamedRule rule = iterator.next();
        if (input.keySet().containsAll(rule.antecedents())) {
          iterator.remove();

          Optional<Optional<Object>> maybeEvaluated = getEvaluator().run(input, rule);
          if (maybeEvaluated.isEmpty()) {
            // TODO better exception
            throw new IllegalArgumentException("Rule evaluation failed");
          }

          Optional<Object> maybeConsequent = maybeEvaluated.get();
          if (maybeConsequent.isPresent()) {
            String consequentName = rule.consequent().orElseThrow();
            Object consequentValue = maybeConsequent.orElseThrow();
            input.put(consequentName, consequentValue);
          }

          changed = true;
        }
      }
    } while (changed);

    return unmodifiableMap(input);
  }

  private RuleEvaluator getEvaluator() {
    return evaluator;
  }
}
