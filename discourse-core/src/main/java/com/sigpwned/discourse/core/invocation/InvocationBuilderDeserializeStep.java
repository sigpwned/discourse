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

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.model.argument.DeserializedArgument;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import com.sigpwned.discourse.core.model.argument.ParsedArgument;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public class InvocationBuilderDeserializeStep<T> {

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final List<ParsedArgument> parsedArguments;

  public InvocationBuilderDeserializeStep(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.parsedArguments = unmodifiableList(parsedArguments);
  }

  public InvocationBuilderPrepareStep<T> deserialize(InvocationContext context) {
    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.beforeDeserialize(rootCommand, dereferencedCommands, resolvedCommand,
          parsedArguments, context);
    });

    List<DeserializedArgument> deserializedArguments = doDeserialize();

    return new InvocationBuilderPrepareStep<>(rootCommand, dereferencedCommands, resolvedCommand,
        deserializedArguments);
  }

  /**
   * extension hook
   */
  protected List<DeserializedArgument> doDeserialize() {
    return parsedArguments.stream().map(a -> {
      Object deserialized = a.parameter().getDeserializer().deserialize(a.argumentText());
      return new DeserializedArgument(a.coordinate(), a.parameter(), a.argumentText(),
          deserialized);
    }).toList();
  }
}
