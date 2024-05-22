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
package com.sigpwned.discourse.core.invocation.phase.factory;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public interface FactoryPhaseListener {
  // RULES STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeFactoryPhaseRulesStep(RulesEngine rulesEngine,
      Map<String, Object> initialState, List<NamedRule> rules) {}

  default void afterFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, Map<String, Object> newState) {}

  default void catchFactoryPhaseRulesStep(Throwable problem) {}

  default void finallyFactoryPhaseRulesStep() {}

  // CREATE STEP //////////////////////////////////////////////////////////////////////////////////
  default void beforeFactoryPhaseCreateStep(Map<String, Object> newState) {}

  default void afterFactoryPhaseCreateStep(Map<String, Object> newState, Object instance) {}

  default void catchFactoryPhaseCreateStep(Throwable problem) {}

  default void finallyFactoryPhaseCreateStep() {}
}
