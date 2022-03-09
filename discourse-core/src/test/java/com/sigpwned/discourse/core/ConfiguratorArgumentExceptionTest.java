package com.sigpwned.discourse.core;

import java.util.Objects;
import org.junit.Test;
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

public class ConfiguratorArgumentExceptionTest {
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  @Configurable
  public static class ConstructorFailureExample {
    private static boolean first = true;

    public ConstructorFailureExample() {
      if (!first)
        throw new RuntimeException("simulated failure");
      first = false;
    }

    @PositionalParameter(position = 0)
    public String example;
  }

  @Test(expected = NewInstanceFailureArgumentException.class)
  public void constructorFailureExample() {
    new Configurator<>(ConstructorFailureExample.class).done().args("hello");
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
    new Configurator<>(PositionalAssignmentFailureExample.class).done().args("hello");
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
    new Configurator<>(OptionAssignmentFailureExample.class).done().args("-x", "hello");
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
    new Configurator<>(FlagAssignmentFailureExample.class).done().args("-x");
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

    Command<EnvironmentAssignmentFailureExample> command =
        new Configurator<>(EnvironmentAssignmentFailureExample.class).done();

    command.setGetEnv(name -> name.equals("HELLO") ? hello : System.getenv(name));

    command.args();
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

    Command<PropertyAssignmentFailureExample> command =
        new Configurator<>(PropertyAssignmentFailureExample.class).done();

    command.setGetProperty(name -> name.equals("hello") ? hello : System.getProperty(name));

    command.args();
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
    new Configurator<>(MissingRequiredExample.class).done().args();
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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
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
      if (this == obj)
        return true;
      if (!super.equals(obj))
        return false;
      if (getClass() != obj.getClass())
        return false;
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
      if (this == obj)
        return true;
      if (!super.equals(obj))
        return false;
      if (getClass() != obj.getClass())
        return false;
      BravoMultiExample other = (BravoMultiExample) obj;
      return Objects.equals(bravo, other.bravo);
    }
  }

  /**
   * Empty arguments contain no subcommand
   */
  @Test(expected = NoSubcommandArgumentException.class)
  public void multiExampleNoSubcommands() {
    new Configurator<>(MultiExample.class).done().args();
  }

  /**
   * The subcommand charlie does not exist
   */
  @Test(expected = UnrecognizedSubcommandArgumentException.class)
  public void multiExampleUnknownSubcommand() {
    new Configurator<>(MultiExample.class).done().args("charlie");
  }

  /**
   * The subcommand - is not valid
   */
  @Test(expected = InvalidDiscriminatorArgumentException.class)
  public void multiExampleInvalidSubcommand() {
    new Configurator<>(MultiExample.class).done().args("-");
  }
}
