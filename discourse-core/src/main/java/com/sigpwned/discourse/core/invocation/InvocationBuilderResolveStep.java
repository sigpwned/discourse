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

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.syntax.InsufficientDiscriminatorsSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.InvalidDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import java.util.ArrayList;
import java.util.List;

public class InvocationBuilderResolveStep<T> {

  private final Command<T> command;

  public InvocationBuilderResolveStep(Command<T> command) {
    this.command = requireNonNull(command);
  }

  public Command<T> getCommand() {
    return command;
  }

  public InvocationBuilderParseStep<T> resolve(List<String> arguments, InvocationContext context) {
    context.set(InvocationContext.ARGUMENTS_KEY, List.copyOf(arguments));

    context.get(InvocationContext.DISCOURSE_LISTENER_CHAIN_KEY).ifPresent(listenerChain -> {
      listenerChain.beforeResolve(getCommand(), arguments, context);
    });

    ResolvedCommandAndRemainingArguments<T> resolved = doResolve(arguments, context);

    return new InvocationBuilderParseStep<T>(getCommand(), resolved.dereferencedCommands(),
        resolved.resolvedCommand(), resolved.remainingArguments());
  }

  protected static record ResolvedCommandAndRemainingArguments<T>(
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArguments) {

    public ResolvedCommandAndRemainingArguments(
        List<MultiCommandDereference<? extends T>> dereferencedCommands,
        SingleCommand<? extends T> resolvedCommand, List<String> remainingArguments) {
      this.dereferencedCommands = unmodifiableList(dereferencedCommands);
      this.resolvedCommand = requireNonNull(resolvedCommand);
      this.remainingArguments = unmodifiableList(remainingArguments);
    }
  }

  /**
   * extension hook
   */
  protected ResolvedCommandAndRemainingArguments<T> doResolve(List<String> arguments,
      InvocationContext context) {
    arguments = new ArrayList<>(arguments);

    Command<? extends T> subcommand = command;
    List<MultiCommandDereference<? extends T>> subcommands = new ArrayList<>();
    while (subcommand instanceof MultiCommand<? extends T> multi) {
      if (arguments.isEmpty()) {
        throw new InsufficientDiscriminatorsSyntaxException(multi);
      }

      String discriminatorString = arguments.remove(0);

      Discriminator discriminator;
      try {
        discriminator = Discriminator.fromString(discriminatorString);
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorSyntaxException(multi, discriminatorString);
      }

      Command<? extends T> dereferencedSubcommand = multi.getSubcommands().get(discriminator);
      if (dereferencedSubcommand == null) {
        throw new UnrecognizedDiscriminatorSyntaxException(multi, discriminator);
      }

      subcommands.add(new MultiCommandDereference<>(multi, discriminator));

      subcommand = dereferencedSubcommand;
    }

    SingleCommand<? extends T> single = (SingleCommand<? extends T>) subcommand;

    return new ResolvedCommandAndRemainingArguments<>(subcommands, single, arguments);
  }
}
