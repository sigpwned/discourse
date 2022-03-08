package com.sigpwned.discourse.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidCollectionParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidLongNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPropertyNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidShortNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidVariableNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;

public class ConfiguratorTest {
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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Example1 other = (Example1) obj;
      return flag == other.flag && Objects.equals(option, other.option)
          && Objects.equals(position0, other.position0);
    }
  }

  @Test
  public void test1() {
    final String alpha = "alpha";
    final String bravo = "bravo";

    Example1 observed =
        new Configurator<>(Example1.class).args(asList("-f", "-o", alpha, bravo)).done();

    Example1 expected = new Example1();
    expected.flag = true;
    expected.option = alpha;
    expected.position0 = bravo;

    assertThat(observed, is(expected));
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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Example2 other = (Example2) obj;
      return flag == other.flag && Objects.equals(option, other.option)
          && Objects.equals(positions, other.positions);
    }
  }

  @Test
  public void test2() {
    final String alpha = "alpha";
    final String bravo = "bravo";
    final String charlie = "charlie";

    Example2 observed =
        new Configurator<>(Example2.class).args(asList("-f", "-o", alpha, bravo, charlie)).done();

    Example2 expected = new Example2();
    expected.flag = true;
    expected.option = alpha;
    expected.positions = asList(bravo, charlie);

    assertThat(observed, is(expected));
  }

  /**
   * Gap in positional arguments
   */
  @Configurable
  public static class Example3 {
    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 1)
    public String position1;
  }

  @Test(expected = MissingPositionConfigurationException.class)
  public void test3() {
    final String alpha = "alpha";
    final String bravo = "bravo";
    final String charlie = "charlie";

    new Configurator<>(Example3.class).args(asList("-f", "-o", alpha, bravo, charlie)).done();
  }

  @Configurable
  public static class TooManyAnnotationsExample {
    // Note that we have two annotations on this field, which is illegal
    @OptionParameter(shortName = "o", longName = "option")
    @FlagParameter(shortName = "f", longName = "flag")
    public boolean example;
  }

  /**
   * We should get an exception for having too many annotations on our field
   */
  @Test(expected = TooManyAnnotationsConfigurationException.class)
  public void tooManyAnnotationsTest() {
    new Configurator<>(TooManyAnnotationsExample.class).args(asList("-x")).done();
  }

  // Note that this is not marked @Configurable
  public static class NotConfigurableExample {
    @OptionParameter(shortName = "o", longName = "option")
    public boolean example;
  }

  /**
   * We should get an exception for having too many annotations on our field
   */
  @Test(expected = NotConfigurableConfigurationException.class)
  public void notConfigurableTest() {
    new Configurator<>(NotConfigurableExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidOptionShortNameExample {
    @OptionParameter(shortName = "-")
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidShortNameConfigurationException.class)
  public void invalidOptionShortNameExample() {
    new Configurator<>(InvalidOptionShortNameExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidOptionLongNameExample {
    @OptionParameter(longName = "-")
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidLongNameConfigurationException.class)
  public void invalidOptionLongNameExample() {
    new Configurator<>(InvalidOptionLongNameExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class NoNameOptionExample {
    @OptionParameter
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = NoNameConfigurationException.class)
  public void optionNoNameExample() {
    new Configurator<>(NoNameOptionExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidFlagShortNameExample {
    @FlagParameter(shortName = "-")
    public boolean example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidShortNameConfigurationException.class)
  public void invalidFlagShortNameExample() {
    new Configurator<>(InvalidFlagShortNameExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidFlagLongNameExample {
    @FlagParameter(longName = "-")
    public boolean example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidLongNameConfigurationException.class)
  public void invalidFlagLongNameExample() {
    new Configurator<>(InvalidFlagLongNameExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class NoNameFlagExample {
    @FlagParameter
    public boolean example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = NoNameConfigurationException.class)
  public void flagNoNameExample() {
    new Configurator<>(NoNameFlagExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidVariableExample {
    @EnvironmentParameter(variableName = "")
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidVariableNameConfigurationException.class)
  public void invalidVariableExample() {
    new Configurator<>(InvalidVariableExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidPropertyExample {
    @PropertyParameter(propertyName = "")
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidPropertyNameConfigurationException.class)
  public void invalidPropertyExample() {
    new Configurator<>(InvalidPropertyExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class DuplicateCoordinateExample {
    @OptionParameter(shortName = "x")
    public String example1;

    @OptionParameter(shortName = "x")
    public String example2;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = DuplicateCoordinateConfigurationException.class)
  public void duplicateShortNameExample() {
    new Configurator<>(DuplicateCoordinateExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidPositionExample {
    @PositionalParameter(position = -1)
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidPositionConfigurationException.class)
  public void invalidPositionExample() {
    new Configurator<>(InvalidPositionExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class SkipPositionExample {
    @PositionalParameter(position = 0)
    public String example1;

    @PositionalParameter(position = 2)
    public String example2;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = MissingPositionConfigurationException.class)
  public void skipPositionExample() {
    new Configurator<>(SkipPositionExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class NoZeroPositionExample {
    @PositionalParameter(position = 1)
    public String example1;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = MissingPositionConfigurationException.class)
  public void noZeroPositionExample() {
    new Configurator<>(SkipPositionExample.class).args(asList("-x")).done();
  }

  @Configurable
  public static class InvalidCollectionPositionExample {
    @PositionalParameter(position = 0)
    public List<String> example1;

    @PositionalParameter(position = 1)
    public String example2;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = InvalidCollectionParameterPlacementConfigurationException.class)
  public void invalidCollectionPositionExample() {
    new Configurator<>(InvalidCollectionPositionExample.class).args(asList("-x")).done();
  }

  /**
   * Required positional after optional positional
   */
  @Configurable
  public static class InvalidRequiredPositionExample {
    @PositionalParameter(position = 0, required = false)
    public String position0;

    @PositionalParameter(position = 1, required = true)
    public String position1;
  }

  @Test(expected = InvalidRequiredParameterPlacementConfigurationException.class)
  public void invalidRequiredPositionExample() {
    final String alpha = "alpha";
    final String bravo = "bravo";
    final String charlie = "charlie";

    new Configurator<>(InvalidRequiredPositionExample.class)
        .args(asList("-f", "-o", alpha, bravo, charlie)).done();
  }

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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AllowUnconfiguredFieldExample other = (AllowUnconfiguredFieldExample) obj;
      return Objects.equals(example1, other.example1) && Objects.equals(example2, other.example2);
    }
  }

  /**
   * Invalid option short name
   */
  @Test
  public void allowUnconfiguredFieldExample() {
    final String hello = "hello";

    AllowUnconfiguredFieldExample observed =
        new Configurator<>(AllowUnconfiguredFieldExample.class).args(asList(hello)).done();

    AllowUnconfiguredFieldExample expected = new AllowUnconfiguredFieldExample();
    expected.example2 = hello;

    assertThat(observed, is(expected));
  }

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

  /**
   * Invalid option short name
   */
  @Test(expected = NewInstanceFailureArgumentException.class)
  public void constructorFailureExample() {
    new Configurator<>(ConstructorFailureExample.class).args(asList("hello")).done();
  }

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

  /**
   * Invalid option short name
   */
  @Test(expected = AssignmentFailureArgumentException.class)
  public void positionalAssignmentFailureExample() {
    new Configurator<>(PositionalAssignmentFailureExample.class).args(asList("hello")).done();
  }

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

  /**
   * Invalid option short name
   */
  @Test(expected = AssignmentFailureArgumentException.class)
  public void optionAssignmentFailureExample() {
    new Configurator<>(OptionAssignmentFailureExample.class).args(asList("-x", "hello")).done();
  }

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

  /**
   * Invalid option short name
   */
  @Test(expected = AssignmentFailureArgumentException.class)
  public void flagAssignmentFailureExample() {
    new Configurator<>(FlagAssignmentFailureExample.class).args(asList("-x")).done();
  }

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

  /**
   * Environment test assignment failure
   */
  @Test(expected = AssignmentFailureArgumentException.class)
  public void environmentAssignmentFailureExample() {
    new Configurator<>(EnvironmentAssignmentFailureExample.class) {
      @Override
      protected String systemGetenv(String name) {
        return name.equals("HELLO") ? "test" : super.systemGetenv(name);
      }
    }.args(asList()).done();
  }

  @Configurable
  public static class PropertyAssignmentFailureExample {
    @PropertyParameter(propertyName = "HELLO")
    private String example;

    public String getExample() {
      return example;
    }

    public void setExample(String example) {
      throw new RuntimeException("simulated failiure");
    }
  }

  /**
   * Environment test assignment failure
   */
  @Test(expected = AssignmentFailureArgumentException.class)
  public void propertyAssignmentFailureExample() {
    new Configurator<>(PropertyAssignmentFailureExample.class) {
      @Override
      protected String systemGetProperty(String key) {
        return key.equals("HELLO") ? "test" : super.systemGetProperty(key);
      }
    }.args(asList()).done();
  }

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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AccessorExample other = (AccessorExample) obj;
      return Objects.equals(example, other.example);
    }
  }

  /**
   * Invalid option short name
   */
  @Test
  public void accessorExample() {
    final String hello = "hello";

    AccessorExample observed = new Configurator<>(AccessorExample.class).args(asList(hello)).done();

    AccessorExample expected = new AccessorExample();
    expected.example = hello;

    assertThat(observed, is(expected));
  }

  @Configurable
  public static class MissingRequiredExample {
    @PositionalParameter(position = 0, required = true)
    public String example;
  }

  /**
   * Invalid option short name
   */
  @Test(expected = UnassignedRequiredParametersArgumentException.class)
  public void missingRequiredExample() {
    new Configurator<>(AccessorExample.class).args(new String[0]).done();
  }

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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      PrimitivesExample other = (PrimitivesExample) obj;
      return Arrays.equals(examples, other.examples) && x == other.x;
    }
  }

  /**
   * We should handle primitives well
   */
  @Test
  public void primitivesExample() {
    PrimitivesExample observed =
        new Configurator<>(PrimitivesExample.class).args("-x", "1", "2", "3").done();

    PrimitivesExample expected = new PrimitivesExample();
    expected.x = 1;
    expected.examples = new int[] {2, 3};

    assertThat(observed, is(expected));
  }

  @Configurable
  public static class EnvironmentExample {
    @EnvironmentParameter(variableName = "HELLO")
    public String hello;

    @Override
    public int hashCode() {
      return Objects.hash(hello);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      EnvironmentExample other = (EnvironmentExample) obj;
      return Objects.equals(hello, other.hello);
    }
  }

  /**
   * We should handle environment variables
   */
  @Test
  public void environmentExample() {
    final String hello = "hello";

    EnvironmentExample observed = new Configurator<>(EnvironmentExample.class) {
      @Override
      protected String systemGetenv(String name) {
        return name.equals("HELLO") ? hello : super.systemGetenv(name);
      }
    }.args().done();

    EnvironmentExample expected = new EnvironmentExample();
    expected.hello = hello;

    assertThat(observed, is(expected));
  }

  @Configurable
  public static class PropertyExample {
    @PropertyParameter(propertyName = "hello")
    public String hello;

    @Override
    public int hashCode() {
      return Objects.hash(hello);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      PropertyExample other = (PropertyExample) obj;
      return Objects.equals(hello, other.hello);
    }
  }

  /**
   * We should handle system properties
   */
  @Test
  public void propertyExample() {
    final String hello = "hello";

    PropertyExample observed = new Configurator<>(PropertyExample.class) {
      @Override
      protected String systemGetProperty(String name) {
        return name.equals("hello") ? hello : super.systemGetenv(name);
      }
    }.args().done();

    PropertyExample expected = new PropertyExample();
    expected.hello = hello;

    assertThat(observed, is(expected));
  }
}
