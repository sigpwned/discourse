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

public class VersionPrintingInvocationStrategyTest {
//
//  @Configurable(name = "foobar", version = "1.0.0")
//  public static class Example extends StandardConfigurationBase {
//
//  }
//
//  @Test
//  public void givenVersionArgs_whenInvoke_thenDoPrint() {
//    Command<Example> command = Command.scan(Example.class);
//
//    ByteArrayOutputStream err = new ByteArrayOutputStream();
//
//    InvocationContext context = new DefaultInvocationContext();
//    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));
//
//    boolean exited = false;
//    try {
//      new VersionPrintingInvocationStrategy(new SingleCommandInvocationStrategy()) {
//        @Override
//        protected ExitError exit(int status) {
//          return new ExitError(status);
//        }
//      }.invoke(command, context, List.of("--version"));
//    } catch (ExitError e) {
//      exited = true;
//    }
//
//    assertThat(exited, is(true));
//    assertThat(err.toString(StandardCharsets.UTF_8), CoreMatchers.containsString("foobar 1.0.0"));
//  }
//
//  @Test
//  public void givenNonVersionArgs_whenInvoke_thenDontPrint() {
//    Command<Example> command = Command.scan(Example.class);
//
//    ByteArrayOutputStream err = new ByteArrayOutputStream();
//
//    InvocationContext context = new DefaultInvocationContext();
//    context.set(InvocationContext.ERROR_STREAM_KEY, new PrintStream(err));
//
//    boolean exited = false;
//    try {
//      new VersionPrintingInvocationStrategy(new SingleCommandInvocationStrategy()) {
//        @Override
//        protected ExitError exit(int status) {
//          return new ExitError(status);
//        }
//      }.invoke(command, context, List.of());
//    } catch (ExitError e) {
//      exited = true;
//    }
//
//    assertThat(exited, is(false));
//    assertThat(err.toString(StandardCharsets.UTF_8), is(""));
//  }
}
