package com.sigpwned.discourse.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.exception.argument.UnrecognizedShortNameSyntaxException;
import com.sigpwned.discourse.core.property.FlagConfigurationProperty;
import com.sigpwned.discourse.core.property.OptionConfigurationProperty;
import com.sigpwned.discourse.core.property.PositionalConfigurationProperty;

public class ArgsParserTest {
  public static class Example {
    @FlagParameter(shortName = "f", longName = "flag")
    public boolean flag;

    @OptionParameter(shortName = "o", longName = "option")
    public String option;

    @PositionalParameter(position = 0)
    public String position0;
  }

  /**
   * short flag, long disconnected option, positional
   */
  @Test
  public void test1() {
    ConfigurationClass cc = ConfigurationClass.scan(new StorageContext(), Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final var flag = new AtomicBoolean(false);
    final var option = new AtomicReference<>();
    final var position0 = new AtomicReference<>();
    new ArgsParser(cc, new ArgsParser.Handler() {
      
      @Override
      public void flag(FlagConfigurationProperty property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationProperty property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationProperty property, String value) {
        position0.set(value);
      }
    }).parse(asList("-f", "--option", alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * long flag, long connected option, positional
   */
  @Test
  public void test2() {
    ConfigurationClass cc = ConfigurationClass.scan(new StorageContext(), Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final var flag = new AtomicBoolean(false);
    final var option = new AtomicReference<>();
    final var position0 = new AtomicReference<>();
    new ArgsParser(cc, new ArgsParser.Handler() {
      @Override
      public void flag(FlagConfigurationProperty property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationProperty property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationProperty property, String value) {
        position0.set(value);
      }
    }).parse(asList("--flag", "--option=" + alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * long flag, short option, positional
   */
  @Test
  public void test3() {
    ConfigurationClass cc = ConfigurationClass.scan(new StorageContext(), Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final var flag = new AtomicBoolean(false);
    final var option = new AtomicReference<>();
    final var position0 = new AtomicReference<>();
    new ArgsParser(cc, new ArgsParser.Handler() {
      @Override
      public void flag(FlagConfigurationProperty property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationProperty property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationProperty property, String value) {
        position0.set(value);
      }
    }).parse(asList("--flag", "-o", alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unrecognized option
   */
  @Test(expected = UnrecognizedShortNameSyntaxException.class)
  public void test4() {
    ConfigurationClass cc = ConfigurationClass.scan(new StorageContext(), Example.class);

    new ArgsParser(cc, new ArgsParser.Handler() {}).parse(asList("-x"));
  }
}
