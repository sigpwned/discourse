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
package com.sigpwned.discourse.core.invocation.model;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.command.SubCommand;

public record CommandResolution<T>(RootCommand<? super T> rootCommand, Command<T> resolvedCommand,
    List<CommandDereference<? super T>> dereferences, List<String> remainingArgs) {

  public CommandResolution {
    rootCommand = requireNonNull(rootCommand);
    resolvedCommand = requireNonNull(resolvedCommand);
    dereferences = unmodifiableList(dereferences);
    remainingArgs = unmodifiableList(remainingArgs);

    Command<?> command = rootCommand;
    for (CommandDereference<?> dereference : dereferences) {
      SubCommand<?> subcommand = command.getSubcommands().get(dereference.discriminator());
      if (subcommand == null) {
        throw new IllegalArgumentException("Invalid dereference: " + dereference);
      }
      if (subcommand != dereference.command()) {
        throw new IllegalArgumentException("Invalid dereference: " + dereference);
      }
      command = subcommand;
    }
    if (command != resolvedCommand) {
      throw new IllegalArgumentException("Invalid resolved command: " + resolvedCommand);
    }
  }
}
