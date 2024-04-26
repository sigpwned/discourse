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
package com.sigpwned.discourse.core.invocation.strategy;

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.exception.syntax.InsufficientDiscriminatorsSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.InvalidDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@link InvocationStrategy} that dereferences multi commands using discriminators. It
 * interprets the longest possible prefix of the arguments as a sequence of discriminators into a
 * {@link MultiCommand}, and then invokes resolved command with the remaining arguments. If the
 * given command is not a {@code MultiCommand}, then it is simply invoked directly.
 */
public class SubcommandDereferencingInvocationStrategy implements InvocationStrategy {

  private final InvocationStrategy delegate;

  public SubcommandDereferencingInvocationStrategy(InvocationStrategy delegate) {
    this.delegate = Objects.requireNonNull(delegate);
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args) {
    args = new ArrayList<>(args);

    Command<? extends T> subcommand = command;
    List<Map.Entry<Discriminator, MultiCommand<? extends T>>> subcommands = new ArrayList<>();
    while (subcommand instanceof MultiCommand<? extends T> multi) {
      if (args.isEmpty()) {
        // TODO This should really be "insufficient" not "no"
        throw new InsufficientDiscriminatorsSyntaxException(multi);
      }

      String discriminatorString = args.remove(0);

      Discriminator discriminator;
      try {
        discriminator = Discriminator.fromString(discriminatorString);
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorSyntaxException(multi, discriminatorString);
      }

      subcommands.add(Map.entry(discriminator, multi));

      subcommand = Optional.of(multi).map(m -> m.getSubcommands().get(discriminator))
          .orElseThrow(() -> new UnrecognizedDiscriminatorSyntaxException(multi, discriminator));
    }

    final Invocation<? extends T> invocation = getDelegate().invoke(subcommand, context, args);

    return new Invocation<>(subcommands, invocation.getLeafCommand(), invocation.getLeafArgs(),
        invocation.getConfiguration());
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }
}
