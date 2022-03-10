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

public class ConfiguratorSingleCommandTest {
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
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
        new CommandBuilder().build(Example1.class).args("-f", "-o", alpha, bravo).configuration();

    Example1 expected = new Example1();
    expected.flag = true;
    expected.option = alpha;
    expected.position0 = bravo;

    assertThat(observed, is(expected));
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
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

    Example2 observed = new CommandBuilder().build(Example2.class)
        .args("-f", "-o", alpha, bravo, charlie).configuration();

    Example2 expected = new Example2();
    expected.flag = true;
    expected.option = alpha;
    expected.positions = asList(bravo, charlie);

    assertThat(observed, is(expected));
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

  @Test
  public void allowUnconfiguredFieldExample() {
    final String hello = "hello";

    AllowUnconfiguredFieldExample observed =
        new CommandBuilder().build(AllowUnconfiguredFieldExample.class).args(hello).configuration();

    AllowUnconfiguredFieldExample expected = new AllowUnconfiguredFieldExample();
    expected.example2 = hello;

    assertThat(observed, is(expected));
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

  @Test
  public void accessorExample() {
    final String hello = "hello";

    AccessorExample observed =
        new CommandBuilder().build(AccessorExample.class).args(hello).configuration();

    AccessorExample expected = new AccessorExample();
    expected.example = hello;

    assertThat(observed, is(expected));
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

  @Test
  public void primitivesExample() {
    PrimitivesExample observed = new CommandBuilder().build(PrimitivesExample.class)
        .args("-x", "1", "2", "3").configuration();

    PrimitivesExample expected = new PrimitivesExample();
    expected.x = 1;
    expected.examples = new int[] {2, 3};

    assertThat(observed, is(expected));
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
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

  @Test
  public void environmentExample() {
    final String hello = "hello";

    Invocation<EnvironmentExample> invocation = new CommandBuilder().build(EnvironmentExample.class).args();

    invocation.setGetEnv(name -> name.equals("HELLO") ? hello : System.getenv(name));

    EnvironmentExample observed = invocation.configuration();

    EnvironmentExample expected = new EnvironmentExample();
    expected.hello = hello;

    assertThat(observed, is(expected));
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
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

  @Test
  public void propertyExample() {
    final String hello = "hello";

    Invocation<PropertyExample> invocation = new CommandBuilder().build(PropertyExample.class).args();

    invocation.setGetProperty(name -> name.equals("hello") ? hello : System.getProperty(name));

    PropertyExample observed = invocation.configuration();

    PropertyExample expected = new PropertyExample();
    expected.hello = hello;

    assertThat(observed, is(expected));
  }
}
