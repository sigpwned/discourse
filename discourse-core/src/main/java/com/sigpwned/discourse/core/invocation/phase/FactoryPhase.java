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
package com.sigpwned.discourse.core.invocation.phase;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.factory.exception.NoInstanceException;
import com.sigpwned.discourse.core.invocation.phase.factory.exception.RuleFailureException;
import com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public class FactoryPhase {

  private final FactoryPhaseListener listener;

  public FactoryPhase(FactoryPhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  public final Object create(List<NamedRule> rules, Map<String, Object> initialState,
      InvocationContext context) {
    Map<String, Object> finalState = doRulesStep(initialState, rules, context);

    Object instance = doFactoryStep(finalState, context);

    return instance;
  }

  private Map<String, Object> doRulesStep(Map<String, Object> initialState, List<NamedRule> rules,
      InvocationContext context) {
    RulesEngine rulesEngine = context.getRulesEngine();

    Map<String, Object> newState;
    try {
      getListener().beforeFactoryPhaseRulesStep(rulesEngine, initialState, rules);
      newState = rulesStep(rulesEngine, initialState, rules, context);
      getListener().afterFactoryPhaseRulesStep(rulesEngine, initialState, rules, newState);
    } catch (Throwable problem) {
      getListener().catchFactoryPhaseRulesStep(problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhaseRulesStep();
    }

    return newState;
  }

  protected Map<String, Object> rulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, InvocationContext context) {
    Map<String, Object> finalState;
    try {
      finalState = rulesEngine.run(initialState, rules);
    } catch (Exception e) {
      throw new RuleFailureException(e);
    }
    return finalState;
  }

  private Object doFactoryStep(Map<String, Object> newState, InvocationContext context) {
    Object instance;
    try {
      getListener().beforeFactoryPhaseCreateStep(newState);
      instance = factoryStep(newState, context);
      getListener().afterFactoryPhaseCreateStep(newState, instance);
    } catch (Throwable problem) {
      getListener().catchFactoryPhaseCreateStep(problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhaseCreateStep();
    }

    return instance;
  }

  protected Object factoryStep(Map<String, Object> finalState, InvocationContext context) {
    // TODO instance name constant
    Object instance = finalState.get("");
    if (instance == null) {
      if (finalState.containsKey("")) {
        // The rules computed an instance, but it's null. That's an NPE.
        throw new NullPointerException();
      }
      // The rules didn't compute an instance. This should not happen because we performed a graph
      // analysis that guarantees we invoke a rule that (claims to) produce the instance.
      throw new NoInstanceException();
    }

    return instance;
  }

  private FactoryPhaseListener getListener() {
    return listener;
  }
}
