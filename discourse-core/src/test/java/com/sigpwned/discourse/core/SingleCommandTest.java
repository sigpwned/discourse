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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import com.sigpwned.discourse.core.invocation.strategy.DefaultInvocationStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.Test;

/**
 * Test {@link SingleCommand} specific features
 */
public class SingleCommandTest {
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void test1() {
    final String alpha = "alpha";
    final String bravo = "bravo";

    Example1 observed = DefaultInvocationStrategy.INSTANCE.invoke(
        new CommandBuilder().build(Example1.class), new DefaultInvocationContext(),
        List.of("-f", "-o", alpha, bravo)).getConfiguration();

    Example1 expected = new Example1();
    expected.flag = true;
    expected.option = alpha;
    expected.position0 = bravo;

    assertThat(observed, is(expected));
  }

  @Test
  public void test2() {
    final String alpha = "alpha";
    final String bravo = "bravo";
    final String charlie = "charlie";

    Example2 observed = DefaultInvocationStrategy.INSTANCE.invoke(
        new CommandBuilder().build(Example2.class), new DefaultInvocationContext(),
        List.of("-f", "-o", alpha, bravo, charlie)).getConfiguration();

    Example2 expected = new Example2();
    expected.flag = true;
    expected.option = alpha;
    expected.positions = asList(bravo, charlie);

    assertThat(observed, is(expected));
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void allowUnconfiguredFieldExample() {
    final String hello = "hello";

    AllowUnconfiguredFieldExample observed = DefaultInvocationStrategy.INSTANCE.invoke(
        new CommandBuilder().build(AllowUnconfiguredFieldExample.class),
        new DefaultInvocationContext(), List.of(hello)).getConfiguration();

    AllowUnconfiguredFieldExample expected = new AllowUnconfiguredFieldExample();
    expected.example2 = hello;

    assertThat(observed, is(expected));
  }

  @Test
  public void accessorExample() {
    final String hello = "hello";

    AccessorExample observed = DefaultInvocationStrategy.INSTANCE.invoke(
        new CommandBuilder().build(AccessorExample.class), new DefaultInvocationContext(),
        List.of(hello)).getConfiguration();

    AccessorExample expected = new AccessorExample();
    expected.example = hello;

    assertThat(observed, is(expected));
  }

  @Test
  public void primitivesExample() {
    PrimitivesExample observed = DefaultInvocationStrategy.INSTANCE.invoke(
        new CommandBuilder().build(PrimitivesExample.class), new DefaultInvocationContext(),
        List.of("-x", "1", "2", "3")).getConfiguration();

    PrimitivesExample expected = new PrimitivesExample();
    expected.x = 1;
    expected.examples = new int[]{2, 3};

    assertThat(observed, is(expected));
  }

  /**
   * Bog standard
   */
  @Configurable
  public static class Example1 {

    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 0)
    public String position0;

    @Override
    public int hashCode() {
      return Objects.hash(flag, option, position0);
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
      Example1 other = (Example1) obj;
      return flag == other.flag && Objects.equals(option, other.option) && Objects.equals(position0,
          other.position0);
    }
  }

  /**
   * Collection positional arguments
   */
  @Configurable
  public static class Example2 {

    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 0)
    public List<String> positions;

    @Override
    public int hashCode() {
      return Objects.hash(flag, option, positions);
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
      Example2 other = (Example2) obj;
      return flag == other.flag && Objects.equals(option, other.option) && Objects.equals(positions,
          other.positions);
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class AllowUnconfiguredFieldExample {

    // Note that there is no parameter here
    public String example1;

    @PositionalParameter(position = 0)
    public String example2;

    @Override
    public int hashCode() {
      return Objects.hash(example1, example2);
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
      AllowUnconfiguredFieldExample other = (AllowUnconfiguredFieldExample) obj;
      return Objects.equals(example1, other.example1) && Objects.equals(example2, other.example2);
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class AccessorExample {

    @PositionalParameter(position = 0)
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      this.example = example;
    }

    @Override
    public int hashCode() {
      return Objects.hash(example);
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
      AccessorExample other = (AccessorExample) obj;
      return Objects.equals(example, other.example);
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class PrimitivesExample {

    @OptionParameter(shortName = "x")
    public int x;

    @PositionalParameter(position = 0)
    public int[] examples;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(examples);
      result = prime * result + Objects.hash(x);
      return result;
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
      PrimitivesExample other = (PrimitivesExample) obj;
      return Arrays.equals(examples, other.examples) && x == other.x;
    }
  }
}
