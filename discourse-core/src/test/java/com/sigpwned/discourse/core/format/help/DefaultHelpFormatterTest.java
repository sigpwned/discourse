/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.format.help;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.io.Resources;
import com.sigpwned.discourse.core.invocation.model.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test {@link DefaultHelpFormatter}
 */
@Ignore("actively working on help formatting")
public class DefaultHelpFormatterTest {

  public InvocationContext context;

  @Before
  public void setupDefaultHelpFormatterTest() {
    context = new DefaultInvocationContext();
  }

  @After
  public void cleanupDefaultHelpFormatterTest() {
    context = null;
  }

  @Configurable(name = "test", description = "This is a test. This is only a test.")
  public static class SingleExample {

    @FlagParameter(shortName = "f", longName = "flag", description = "This is a test flag. It is only a test flag. Lorem ipsum dolore sit amet.")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option", description = "This is a test option. It is only a test flag. Lorem ipsum dolore sit amet.")
    public String option;

    @EnvironmentParameter(variableName = "VARIABLE_NAME", description = "This is a test variable. It is only a test flag. Lorem ipsum dolore sit amet.")
    public String variable;

    @PropertyParameter(propertyName = "property.name", description = "This is a test property. It is only a test flag. Lorem ipsum dolore sit amet.")
    public String property;

    @PositionalParameter(position = 0)
    public String position0;
  }

  @Test
  public void givenSingleCommand_whenFormatHelp_thenGenerateExpectedText() throws IOException {
    Command<?> command = Invocation.builder().scan(SingleExample.class, context).getCommand();
    String observed = new DefaultHelpFormatter().formatHelp(command);
    String expected = Resources.toString(getClass().getResource("singlecommandhelp.txt"),
        StandardCharsets.UTF_8);
    assertThat(observed, is(expected));
  }

  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = AlphaMultiExample.class),
      @Subcommand(discriminator = "bravo", configurable = BravoMultiExample.class)}, name = "test", description = "This is a test. This is only a test.")
  public abstract static class MultiExample {

    @FlagParameter(shortName = "f", longName = "flag", description = "This is a test flag. It is only a test flag. Lorem ipsum dolore sit amet.")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option", description = "This is a test option. It is only a test flag. Lorem ipsum dolore sit amet.")
    public String option;

    @EnvironmentParameter(variableName = "VARIABLE_NAME", description = "This is a test variable. It is only a test flag. Lorem ipsum dolore sit amet.")
    public String variable;

    @PropertyParameter(propertyName = "property.name", description = "This is a test property. It is only a test flag. Lorem ipsum dolore sit amet.")
    public String property;
  }

  @Configurable(discriminator = "alpha")
  public static class AlphaMultiExample extends MultiExample {

    @PositionalParameter(position = 0)
    public String alpha;
  }

  @Configurable(discriminator = "bravo")
  public static class BravoMultiExample extends MultiExample {

    @PositionalParameter(position = 0)
    public String bravo;
  }

  @Test
  public void givenMultiCommand_whenFormatHelp_thenGenerateExpectedText() throws IOException {
    Command<?> command = Invocation.builder().scan(MultiExample.class, context).getCommand();
    String observed = new DefaultHelpFormatter().formatHelp(command);
    String expected = Resources.toString(getClass().getResource("multicommandhelp.txt"),
        StandardCharsets.UTF_8);
    assertThat(observed, is(expected));
  }
}
