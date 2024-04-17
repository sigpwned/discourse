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
package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.junit.Test;

public class ConfiguratorMultiCommandTest {

  @Configurable(subcommands = {
      @Subcommand(discriminator = "alpha", configurable = AlphaMultiExample.class),
      @Subcommand(discriminator = "bravo", configurable = BravoMultiExample.class)})
  public abstract static class MultiExample {

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @Override
    public int hashCode() {
      return Objects.hash(option);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      MultiExample other = (MultiExample) obj;
      return Objects.equals(option, other.option);
    }
  }

  @Configurable(discriminator = "alpha")
  public static class AlphaMultiExample extends MultiExample {

    @PositionalParameter(position = 0)
    public String alpha;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(alpha);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      AlphaMultiExample other = (AlphaMultiExample) obj;
      return Objects.equals(alpha, other.alpha);
    }
  }

  @Configurable(discriminator = "bravo")
  public static class BravoMultiExample extends MultiExample {

    @PositionalParameter(position = 0)
    public String bravo;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(bravo);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      BravoMultiExample other = (BravoMultiExample) obj;
      return Objects.equals(bravo, other.bravo);
    }
  }

  /**
   * There is one common parameter
   */
  @Test
  public void multiExampleCommonParameters() {
    MultiCommand<MultiExample> command = (MultiCommand<MultiExample>) new CommandBuilder().build(
        MultiExample.class);
    Set<String> commonParameters = command.getCommonParameters().stream()
        .map(ConfigurationParameter::getName).collect(toSet());

    assertThat(commonParameters, is(singleton("option")));
  }

  /**
   * There are three total parameters
   */
  @Test
  public void multiExampleAllParameters() {
    Set<String> allParameters = new CommandBuilder().build(MultiExample.class).getParameters()
        .stream().map(ConfigurationParameter::getName).collect(toSet());

    Set<String> names = new HashSet<>();
    names.add("option");
    names.add("alpha");
    names.add("bravo");

    assertThat(allParameters, is(names));
  }

  /**
   * There are three total parameters
   */
  @Test
  public void multiExampleSubcommands() {
    MultiCommand<MultiExample> command = (MultiCommand<MultiExample>) new CommandBuilder().build(
        MultiExample.class);

    Set<Discriminator> subcommands = command.listSubcommands();

    Set<Discriminator> discriminators = new HashSet<>();
    discriminators.add(Discriminator.fromString("alpha"));
    discriminators.add(Discriminator.fromString("bravo"));

    assertThat(subcommands, is(discriminators));
  }

  /**
   * We should handle subcommands
   */
  @Test
  public void multiExampleAlpha() {
    final String hello = "hello";
    final String world = "world";

    MultiExample observed = new CommandBuilder().build(MultiExample.class)
        .args("alpha", "-o", hello, world).configuration();

    AlphaMultiExample expected = new AlphaMultiExample();
    expected.option = hello;
    expected.alpha = world;

    assertThat(observed, is(expected));
  }

  /**
   * We should handle subcommands
   */
  @Test
  public void multiExampleBravo() {
    final String hello = "hello";
    final String world = "world";

    MultiExample observed = new CommandBuilder().build(MultiExample.class)
        .args("bravo", "-o", hello, world).configuration();

    BravoMultiExample expected = new BravoMultiExample();
    expected.option = hello;
    expected.bravo = world;

    assertThat(observed, is(expected));
  }
}
