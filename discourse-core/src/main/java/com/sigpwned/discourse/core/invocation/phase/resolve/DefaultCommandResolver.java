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
package com.sigpwned.discourse.core.invocation.phase.resolve;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;

public class DefaultCommandResolver implements CommandResolver {
  public static DefaultCommandResolver INSTANCE = new DefaultCommandResolver();

  @Override
  public <T> CommandResolution<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args) {

    Command<? extends T> command = rootCommand;
    List<CommandDereference<? extends T>> dereferences = new ArrayList<>();
    Iterator<String> iterator = args.iterator();
    while (iterator.hasNext()) {
      String arg = iterator.next();
      Command<? extends T> subcommand = command.getSubcommands().get(arg);
      if (subcommand == null) {
        break;
      }

      dereferences.add(new CommandDereference<>(subcommand, arg));

      command = subcommand;

      iterator.remove();
    }

    return new CommandResolution(rootCommand, command, dereferences, args);
  }
}
