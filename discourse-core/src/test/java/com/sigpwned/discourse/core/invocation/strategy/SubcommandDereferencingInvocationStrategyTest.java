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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.sigpwned.discourse.core.CommandBuilder;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.argument.NoSubcommandArgumentException;
import com.sigpwned.discourse.core.invocation.DefaultInvocation;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SubcommandDereferencingInvocationStrategyTest {

  @Configurable(subcommands = {
      @Subcommand(discriminator = "first", configurable = FirstSubcommandExample.class)})
  public abstract static class RootExample {

  }

  @Configurable(discriminator = "first", subcommands = {
      @Subcommand(discriminator = "second", configurable = SecondSubcommandExample.class)})
  public abstract static class FirstSubcommandExample extends RootExample {

  }

  @Configurable(discriminator = "second")
  public static class SecondSubcommandExample extends FirstSubcommandExample {

    public boolean equals(Object other) {
      return other instanceof SecondSubcommandExample;
    }
  }

  @Test
  public void fullyDereferencedTest() {
    MultiCommand<RootExample> rootCommand = (MultiCommand<RootExample>) new CommandBuilder().build(
        RootExample.class);
    MultiCommand<FirstSubcommandExample> firstSubcommand = (MultiCommand<FirstSubcommandExample>) rootCommand.getSubcommands()
        .get(Discriminator.fromString("first"));
    SingleCommand<SecondSubcommandExample> secondSubcommand = (SingleCommand<SecondSubcommandExample>) firstSubcommand.getSubcommands()
        .get(Discriminator.fromString("second"));

    Invocation<? extends RootExample> invocation = new SubcommandDereferencingInvocationStrategy(
        new SingleCommandInvocationStrategy()).invoke(rootCommand, new DefaultInvocationContext(),
        List.of("first", "second"));

    assertThat(invocation, is(new DefaultInvocation<>(
        List.of(Map.entry(Discriminator.fromString("first"), rootCommand),
            Map.entry(Discriminator.fromString("second"), firstSubcommand)), secondSubcommand,
        List.of(), new SecondSubcommandExample())));
  }

  @Test(expected = NoSubcommandArgumentException.class)
  public void partialDereferenceTest() {
    MultiCommand<RootExample> rootCommand = (MultiCommand<RootExample>) new CommandBuilder().build(
        RootExample.class);

    new SubcommandDereferencingInvocationStrategy(
        new SingleCommandInvocationStrategy()).invoke(rootCommand, new DefaultInvocationContext(),
        List.of("first"));
  }

  @Test(expected = NoSubcommandArgumentException.class)
  public void noDereferenceTest() {
    MultiCommand<RootExample> rootCommand = (MultiCommand<RootExample>) new CommandBuilder().build(
        RootExample.class);

    new SubcommandDereferencingInvocationStrategy(
        new SingleCommandInvocationStrategy()).invoke(rootCommand, new DefaultInvocationContext(),
        List.of());
  }
}
