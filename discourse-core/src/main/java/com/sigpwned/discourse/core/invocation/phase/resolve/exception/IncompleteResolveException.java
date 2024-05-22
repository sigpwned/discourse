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
package com.sigpwned.discourse.core.invocation.phase.resolve.exception;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.resolve.ResolveException;

public class IncompleteResolveException extends ResolveException {
  private static final long serialVersionUID = -7964194735037727444L;

  private final RootCommand<?> rootCommand;
  private final List<CommandDereference<?>> dereferences;
  private final Command<?> resolvedCommand;
  private final List<String> remainingArgs;

  public IncompleteResolveException(RootCommand<?> rootCommand,
      List<CommandDereference<?>> dereferences, Command<?> resolvedCommand,
      List<String> remainingArgs) {
    super("Partial resolution: " + rootCommand + " -> " + dereferences + " -> " + resolvedCommand
        + " -> " + remainingArgs);
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferences = unmodifiableList(dereferences);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.remainingArgs = unmodifiableList(remainingArgs);
  }

  public RootCommand<?> getRootCommand() {
    return rootCommand;
  }

  public List<CommandDereference<?>> getDereferences() {
    return dereferences;
  }

  public Command<?> getResolvedCommand() {
    return resolvedCommand;
  }

  public List<String> getRemainingArgs() {
    return remainingArgs;
  }
}
