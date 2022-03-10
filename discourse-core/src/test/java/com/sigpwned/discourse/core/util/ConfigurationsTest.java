package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Objects;
import org.junit.Test;
import com.sigpwned.discourse.core.StandardConfigurationBase;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;

public class ConfigurationsTest {
  @Configurable
  public static class Example extends StandardConfigurationBase {
    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 0)
    public String position0;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(flag, option, position0);
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
      Example other = (Example) obj;
      return flag == other.flag && Objects.equals(option, other.option)
          && Objects.equals(position0, other.position0);
    }
  }

  @Test
  public void test() {
    final String alpha = "alpha";
    final String bravo = "bravo";
    
    Example observed=Discourse.configuration(Example.class, asList("-f", "-o", alpha, bravo));

    Example expected = new Example();
    expected.flag = true;
    expected.option = alpha;
    expected.position0 = bravo;

    assertThat(observed, is(expected));
  }
}
