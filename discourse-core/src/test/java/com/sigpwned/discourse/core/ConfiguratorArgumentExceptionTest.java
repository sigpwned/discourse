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

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.annotation.Subcommand;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.InvalidDiscriminatorArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NoSubcommandArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnrecognizedSubcommandArgumentException;
import java.util.Objects;
import org.junit.Test;

@SuppressWarnings("ALL")
public class ConfiguratorArgumentExceptionTest {

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class ConstructorFailureExample {

    private static boolean first = true;

    public ConstructorFailureExample() {
      if (!first) {
        throw new RuntimeException("simulated failure");
      }
      first = false;
    }

    @PositionalParameter(position = 0)
    public String example;
  }

  @Test(expected = NewInstanceFailureArgumentException.class)
  public void constructorFailureExample() {
    new CommandBuilder().build(ConstructorFailureExample.class).args("hello").configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class PositionalAssignmentFailureExample {

    @PositionalParameter(position = 0)
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failiure");
    }
  }

  @Test(expected = AssignmentFailureArgumentException.class)
  public void positionalAssignmentFailureExample() {
    new CommandBuilder().build(PositionalAssignmentFailureExample.class).args("hello")
        .configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class OptionAssignmentFailureExample {

    @OptionParameter(shortName = "x")
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failiure");
    }
  }

  @Test(expected = AssignmentFailureArgumentException.class)
  public void optionAssignmentFailureExample() {
    new CommandBuilder().build(OptionAssignmentFailureExample.class).args("-x", "hello")
        .configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class FlagAssignmentFailureExample {

    @FlagParameter(shortName = "x")
    private boolean example;

    public boolean isExample() {
      return example;
    }

    public void setExample(boolean example) {
      throw new RuntimeException("simulated failiure");
    }
  }

  @Test(expected = AssignmentFailureArgumentException.class)
  public void flagAssignmentFailureExample() {
    new CommandBuilder().build(FlagAssignmentFailureExample.class).args("-x").configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class EnvironmentAssignmentFailureExample {

    @EnvironmentParameter(variableName = "HELLO")
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failiure");
    }
  }

  @Test(expected = AssignmentFailureArgumentException.class)
  public void environmentAssignmentFailureExample() {
    final String hello = "hello";

    Invocation<? extends EnvironmentAssignmentFailureExample> invocation =
        new CommandBuilder().build(EnvironmentAssignmentFailureExample.class).args();

    invocation.setGetEnv(name -> name.equals("HELLO") ? hello : System.getenv(name));

    invocation.configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class PropertyAssignmentFailureExample {

    @PropertyParameter(propertyName = "hello")
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failiure");
    }
  }

  @Test(expected = AssignmentFailureArgumentException.class)
  public void propertyAssignmentFailureExample() {
    final String hello = "hello";

    Invocation<? extends PropertyAssignmentFailureExample> invocation =
        new CommandBuilder().build(PropertyAssignmentFailureExample.class).args();

    invocation.setGetProperty(name -> name.equals("hello") ? hello : System.getProperty(name));

    invocation.configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class MissingRequiredExample {

    @PositionalParameter(position = 0, required = true)
    public String example;
  }

  @Test(expected = UnassignedRequiredParametersArgumentException.class)
  public void missingRequiredExample() {
    new CommandBuilder().build(MissingRequiredExample.class).args().configuration();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable(
      subcommands = {@Subcommand(discriminator = "alpha", configurable = AlphaMultiExample.class),
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
   * Empty arguments contain no subcommand
   */
  @Test(expected = NoSubcommandArgumentException.class)
  public void multiExampleNoSubcommands() {
    new CommandBuilder().build(MultiExample.class).args();
  }

  /**
   * The subcommand charlie does not exist
   */
  @Test(expected = UnrecognizedSubcommandArgumentException.class)
  public void multiExampleUnknownSubcommand() {
    new CommandBuilder().build(MultiExample.class).args("charlie");
  }

  /**
   * The subcommand - is not valid
   */
  @Test(expected = InvalidDiscriminatorArgumentException.class)
  public void multiExampleInvalidSubcommand() {
    new CommandBuilder().build(MultiExample.class).args("-");
  }
}
