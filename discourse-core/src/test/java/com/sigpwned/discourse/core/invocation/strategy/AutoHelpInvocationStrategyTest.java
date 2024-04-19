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

import com.sigpwned.discourse.core.ExitError;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class AutoHelpInvocationStrategyTest {

  @Configurable(name = "foobar")
  public static abstract sealed class RootExample extends StandardConfigurationBase permits
      AlphaSubcommandExample, BravoSubcommandExample {

  }

  @Configurable(discriminator = "alpha")
  public static final class AlphaSubcommandExample extends RootExample {

  }

  @Configurable(discriminator = "bravo")
  public static abstract sealed class BravoSubcommandExample extends RootExample permits
      HelpSubcommandExample {

  }

  @Configurable(discriminator = "help")
  public static final class HelpSubcommandExample extends BravoSubcommandExample {

  }

  @Test
  public void givenNoArgs_whenInvoke_thenDoPrintHelp() {
    Command<RootExample> command = Command.scan(RootExample.class);

    ByteArrayOutputStream err = new ByteArrayOutputStream();

    InvocationContext context = new DefaultInvocationContext();
    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));

    boolean exited = false;
    try {
      new AutoHelpInvocationStrategy(
          new SubcommandDereferencingInvocationStrategy(new SingleCommandInvocationStrategy())) {
        @Override
        protected ExitError exit(int status) {
          return new ExitError(status);
        }
      }.invoke(command, context, List.of());
    } catch (ExitError e) {
      exited = true;
    }

    assertThat(exited, is(true));
    assertThat(err.toString(StandardCharsets.UTF_8), CoreMatchers.containsString("foobar"));
  }

  @Test
  public void givenHelpArgsThatDontResolveToSubcommand_whenInvoke_thenDoPrintHelp() {
    Command<RootExample> command = Command.scan(RootExample.class);

    ByteArrayOutputStream err = new ByteArrayOutputStream();

    InvocationContext context = new DefaultInvocationContext();
    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));

    boolean exited = false;
    try {
      new AutoHelpInvocationStrategy(
          new SubcommandDereferencingInvocationStrategy(new SingleCommandInvocationStrategy())) {
        @Override
        protected ExitError exit(int status) {
          return new ExitError(status);
        }
      }.invoke(command, context, List.of("help"));
    } catch (ExitError e) {
      exited = true;
    }

    assertThat(exited, is(true));
    assertThat(err.toString(StandardCharsets.UTF_8), CoreMatchers.containsString("foobar"));
  }

  @Test
  public void givenHelpArgsThatDoResolveToSubcommand_whenInvoke_thenDontPrintHelp() {
    Command<RootExample> command = Command.scan(RootExample.class);

    ByteArrayOutputStream err = new ByteArrayOutputStream();

    InvocationContext context = new DefaultInvocationContext();
    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));

    boolean exited = false;
    try {
      new AutoHelpInvocationStrategy(
          new SubcommandDereferencingInvocationStrategy(new SingleCommandInvocationStrategy())) {
        @Override
        protected ExitError exit(int status) {
          return new ExitError(status);
        }
      }.invoke(command, context, List.of("bravo", "help"));
    } catch (ExitError e) {
      exited = true;
    }

    assertThat(exited, is(false));
    assertThat(err.toString(StandardCharsets.UTF_8), is(""));
  }

  @Test
  public void givenValidNonHelpArgs_whenInvoke_thenDontPrint() {
    Command<RootExample> command = Command.scan(RootExample.class);

    ByteArrayOutputStream err = new ByteArrayOutputStream();

    InvocationContext context = new DefaultInvocationContext();
    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));

    boolean exited = false;
    try {
      new AutoHelpInvocationStrategy(
          new SubcommandDereferencingInvocationStrategy(new SingleCommandInvocationStrategy())) {
        @Override
        protected ExitError exit(int status) {
          return new ExitError(status);
        }
      }.invoke(command, context, List.of("alpha"));
    } catch (ExitError e) {
      exited = true;
    }

    assertThat(exited, is(false));
    assertThat(err.toString(StandardCharsets.UTF_8), is(""));
  }
}
