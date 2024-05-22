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
package com.sigpwned.discourse.core.invocation;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener;

public interface InvocationPipelineListener extends ScanPhaseListener, ResolvePhaseListener,
    ParsePhaseListener, EvalPhaseListener, FactoryPhaseListener {
  // INVOCATION ///////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeInvocation(Class<T> rootClazz, List<String> args) {}

  default <T> void afterInvocation(Class<T> rootClazz, List<String> args,
      Invocation<? extends T> invocation) {}

  default void catchInvocation(Throwable t) {}

  default void finallyInvocation() {}

  // SCAN PHASE ///////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanPhase(Class<T> clazz) {}

  default <T> void afterScanPhase(Class<T> clazz, RootCommand<T> command) {}

  default void catchScanPhase(Throwable problem) {}

  default void finallyScanPhase() {}

  // RESOLVE PHASE ////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeResolvePhase(RootCommand<T> command, List<String> originalArgs) {}

  default <T> void afterResolvePhase(RootCommand<T> command, List<String> originalArgs,
      Command<? extends T> resolvedCommand, List<CommandDereference<? extends T>> dereferences,
      List<String> remainingArgs) {}

  default void catchResolvePhase(Throwable problem) {}

  default void finallyResolvePhase() {}

  // PARSE PHASE //////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeParsePhase(Command<T> resolvedCommand, List<String> remainingArgs) {}

  default <T> void afterParsePhase(Command<T> resolvedCommand, List<String> remainingArgs,
      List<Map.Entry<String, String>> namedArgs) {}

  default void catchParsePhase(Throwable problem) {}

  default void finallyParsePhase() {}

  // EVAL PHASE ///////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeEvalPhase(Command<T> resolvedCommand,
      List<Map.Entry<String, String>> namedArgs) {}

  default <T> void afterEvalPhase(Command<T> resolvedCommand,
      List<Map.Entry<String, String>> namedArgs, Map<String, Object> state) {}

  default void catchEvalPhase(Throwable problem) {}

  default void finallyEvalPhase() {}

  // FACTORY PHASE ////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeFactoryPhase(Command<T> resolvedCommand, Map<String, Object> state) {}

  default <T> void afterFactoryPhase(Command<T> resolvedCommand, Map<String, Object> state,
      T instance) {}

  default void catchFactoryPhase(Throwable problem) {}

  default void finallyFactoryPhase() {}
}
