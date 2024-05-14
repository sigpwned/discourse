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
package com.sigpwned.discourse.core.format.version;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.io.Resources;
import com.sigpwned.discourse.core.invocation.model.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link DefaultVersionFormatter}
 */
public class DefaultVersionFormatterTest {

  public InvocationContext context;

  @Before
  public void setupDefaultHelpFormatterTest() {
    context = new DefaultInvocationContext();
  }

  @After
  public void cleanupDefaultHelpFormatterTest() {
    context = null;
  }

  @Configurable(name = "test", version = "1.0.0")
  public static class Example {

  }

  @Test
  public void givenMultiCommand_whenFormatVersion_thenGenerateExpectedText() throws IOException {
    Command<?> command = Invocation.builder().scan(Example.class, context).getCommand();
    String observed = new DefaultVersionFormatter().formatVersion(command);
    String expected = Resources.toString(getClass().getResource("commandversion.txt"),
        StandardCharsets.UTF_8);
    assertThat(observed, is(expected));
  }
}
