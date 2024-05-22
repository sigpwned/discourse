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
package com.sigpwned.discourse.core.invocation.phase.eval;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface EvalPhaseListener {
  // GROUP STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeEvalPhaseGroupStep(List<Map.Entry<String, String>> parsedArgs) {}

  default void afterEvalPhaseGroupStep(List<Map.Entry<String, String>> parsedArgs,
      Map<String, List<String>> groupedArgs) {}

  default void catchEvalPhaseGroupStep(Throwable problem) {}

  default void finallyEvalPhaseGroupStep() {}

  // EVAL STEP ////////////////////////////////////////////////////////////////////////////////////
  default void beforeEvalPhaseEvalStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {}

  default void afterEvalPhaseEvalStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, Map<String, List<Object>> mappedArgs) {}

  default void catchEvalPhaseEvalStep(Throwable problem) {}

  default void finallyEvalPhaseEvalStep() {}

  // REDUCE STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeEvalPhaseReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {}

  default void afterEvalPhaseReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, Map<String, Object> reducedArgs) {}

  default void catchEvalPhaseReduceStep(Throwable problem) {}

  default void finallyEvalPhaseReduceStep() {}
}
