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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import com.sigpwned.discourse.core.invocation.phase.resolve.CommandResolver;
import com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.resolve.exception.IncompleteResolveException;

public class ResolvePhase {
  private final ResolvePhaseListener listener;

  public ResolvePhase(ResolvePhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  public final <T> CommandResolution<? extends T> resolve(RootCommand<T> rootCommand,
      List<String> args, InvocationContext context) {
    CommandResolution<? extends T> resolution = doResolveStep(rootCommand, args, context);

    return resolution;
  }

  private <T> CommandResolution<? extends T> doResolveStep(RootCommand<T> rootCommand,
      List<String> originalArgs, InvocationContext context) {
    CommandResolution<? extends T> resolution;
    try {
      getListener().beforeResolvePhaseResolveStep(rootCommand, originalArgs);

      CommandResolution<? extends T> r =
          resolveStep(context.getCommandResolver(), rootCommand, originalArgs, context);

      List<CommandDereference<? extends T>> dereferences = new ArrayList(r.dereferences());
      Command<? extends T> resolvedCommand = r.resolvedCommand();
      List<String> remainingArgs = new ArrayList<>(r.remainingArgs());

      getListener().afterResolvePhaseResolveStep(rootCommand, originalArgs, dereferences,
          resolvedCommand, originalArgs);

      resolution = new CommandResolution(rootCommand, resolvedCommand, dereferences, remainingArgs);
    } catch (Throwable problem) {
      getListener().catchResolvePhaseResolveStep(problem);
      throw problem;
    } finally {
      getListener().finallyResolvePhaseResolveStep();
    }

    return resolution;
  }

  protected <T> CommandResolution<? extends T> resolveStep(CommandResolver commandResolver,
      RootCommand<T> rootCommand, List<String> originalArgs, InvocationContext context) {
    CommandResolution resolution =
        commandResolver.resolveCommand(rootCommand, unmodifiableList(originalArgs));

    if (!resolution.resolvedCommand().getSubcommands().isEmpty()) {
      throw new IncompleteResolveException(resolution.rootCommand(), resolution.dereferences(),
          resolution.resolvedCommand(), resolution.remainingArgs());
    }

    return resolution;
  }

  private ResolvePhaseListener getListener() {
    return listener;
  }
}
