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

import com.sigpwned.discourse.core.invocation.model.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.TestModule;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.error.ExitError;
import com.sigpwned.discourse.core.exception.syntax.InsufficientDiscriminatorsSyntaxException;
import com.sigpwned.discourse.core.invocation.InvocationBuilderResolveStep;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SubcommandDereferencingInvocationStrategyTest {

  public InvocationContext context;

  @Before
  public void setupSubcommandDereferencingInvocationStrategyTest() {
    context = new DefaultInvocationContext();
    context.register(new TestModule());
  }

  @After
  public void cleanupSubcommandDereferencingInvocationStrategyTest() {
    context = null;
  }

  @Configurable(subcommands = {
      @Subcommand(discriminator = "first", configurable = FirstAnnotationSubcommandExample.class)})
  public abstract static class RootAnnotationExample {

  }

  @Configurable(discriminator = "first", subcommands = {
      @Subcommand(discriminator = "second", configurable = SecondAnnotationSubcommandExample.class)})
  public abstract static class FirstAnnotationSubcommandExample extends RootAnnotationExample {

  }

  @Configurable(discriminator = "second")
  public static class SecondAnnotationSubcommandExample extends FirstAnnotationSubcommandExample {

    public boolean equals(Object other) {
      return other instanceof SecondAnnotationSubcommandExample;
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void givenArgsThatFullyDereferenceToSingleCommand_whenInvoke_thenSucceed() {
    InvocationBuilderResolveStep<RootAnnotationExample> resolveStep = Invocation.builder()
        .scan(RootAnnotationExample.class, context);

    MultiCommand<RootAnnotationExample> rootCommand = (MultiCommand<RootAnnotationExample>) resolveStep.getCommand();
    MultiCommand<FirstAnnotationSubcommandExample> firstSubcommand = (MultiCommand<FirstAnnotationSubcommandExample>) rootCommand.getSubcommands()
        .get(Discriminator.fromString("first"));
    SingleCommand<SecondAnnotationSubcommandExample> secondSubcommand = (SingleCommand<SecondAnnotationSubcommandExample>) firstSubcommand.getSubcommands()
        .get(Discriminator.fromString("second"));

    Invocation<? extends RootAnnotationExample> invocation = resolveStep.resolve(
            List.of("first", "second"), context).parse(context).deserialize(context).prepare(context)
        .build(context);

    assertThat(invocation, is(new Invocation<>(rootCommand,
        List.of(new MultiCommandDereference<>(rootCommand, Discriminator.fromString("first")),
            new MultiCommandDereference<>(firstSubcommand, Discriminator.fromString("second"))),
        secondSubcommand, new SecondAnnotationSubcommandExample())));
  }

  @Test(expected = InsufficientDiscriminatorsSyntaxException.class)
  public void givenArgsThatPartiallyDereferenceToSingleCommand_whenInvoke_thenFailWithNoSubcommandException() {
    Invocation.builder().scan(RootAnnotationExample.class, context)
        .resolve(List.of("first"), context);
  }

  @Test(expected = ExitError.class)
  public void givenArgsThatNonelyDereferenceToSingleCommand_whenInvoke_thenFailWithExitError() {
    // TODO Does this test belong here?
    Invocation.builder().scan(RootAnnotationExample.class, context).resolve(List.of(), context);
  }
}
