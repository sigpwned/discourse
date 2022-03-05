package com.sigpwned.discourse.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;

public class ConfiguratorTest {
  /**
   * Bog standard
   */
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

  /**
   * Required positional after optional positional
   */
  public static class Example4 {
    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 0, required = false)
    public String position0;

    @PositionalParameter(position = 1, required = true)
    public String position1;
  }

  @Test(expected = InvalidRequiredParameterPlacementConfigurationException.class)
  public void test4() {
    final String alpha = "alpha";
    final String bravo = "bravo";
    final String charlie = "charlie";

    new Configurator<>(Example4.class).args(asList("-f", "-o", alpha, bravo, charlie)).done();
  }
}
