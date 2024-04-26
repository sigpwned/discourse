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

import com.sigpwned.discourse.core.util.error.ExitError;
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

public class HelpPrintingInvocationStrategyTest {

  @Configurable(name = "foobar")
  public static class Example extends StandardConfigurationBase {

  }

  @Test
  public void givenHelpArgs_whenInvoke_thenDoPrint() {
    Command<Example> command = Command.scan(Example.class);

    ByteArrayOutputStream err = new ByteArrayOutputStream();

    InvocationContext context = new DefaultInvocationContext();
    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));

    boolean exited = false;
    try {
      new HelpPrintingInvocationStrategy(new SingleCommandInvocationStrategy()) {
        @Override
        protected ExitError exit(int status) {
          return new ExitError(status);
        }
      }.invoke(command, context, List.of("--help"));
    } catch (ExitError e) {
      exited = true;
    }

    assertThat(exited, is(true));
    assertThat(err.toString(StandardCharsets.UTF_8), CoreMatchers.containsString("foobar"));
  }

  @Test
  public void givenNonHelpArgs_whenInvoke_thenDontPrint() {
    Command<Example> command = Command.scan(Example.class);

    ByteArrayOutputStream err = new ByteArrayOutputStream();

    InvocationContext context = new DefaultInvocationContext();
    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));

    boolean exited = false;
    try {
      new HelpPrintingInvocationStrategy(new SingleCommandInvocationStrategy()) {
        @Override
        protected ExitError exit(int status) {
          return new ExitError(status);
        }
      }.invoke(command, context, List.of());
    } catch (ExitError e) {
      exited = true;
    }

    assertThat(exited, is(false));
    assertThat(err.toString(StandardCharsets.UTF_8), is(""));
  }
}
