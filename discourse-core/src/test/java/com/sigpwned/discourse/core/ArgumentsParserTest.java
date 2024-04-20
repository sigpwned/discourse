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
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.exception.syntax.FlagValuePresentSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.OptionValueMissingSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedSwitchSyntaxException;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

/**
 * Tests for {@link ArgumentsParser}.
 */
public class ArgumentsParserTest {

  @Configurable
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
  public void givenShortFlagThenLongDisconnectedOptionThenPositional_whenParse_thenSucceed() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
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
  public void givenLongFlagThenLongConnectedOptionThenPositional_whenParse_thenSucceed() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
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
  public void givenLongFlagThenShortOptionThenPositional_whenParse_thenSucceed() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
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
  @Test(expected = UnrecognizedSwitchSyntaxException.class)
  public void givenUnrecognizedShortOption_whenParse_thenFailWithUnrecognizedShortNameException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
    }).parse(List.of("-x"));
  }

  /**
   * short flag, long connected option, positional
   */
  @Test
  public void givenShortFlagThenLongConnectedOptionThenPositional_whenParse_thenSucceed() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("-f", "--option=" + alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * two bundled short flags, option, positional
   */
  @Test
  public void givenBundleThenOptionValueThenPositional_whenParse_thenSucceed() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("-fo", alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unexpected eof looking for option value after short name
   */
  @Test(expected = OptionValueMissingSyntaxException.class)
  public void givenShortSwitchThenEof_whenParse_thenFailWithMissingShortNameValueException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("-o"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unexpected eof looking for option value after long name
   */
  @Test(expected = OptionValueMissingSyntaxException.class)
  public void givenLongSwitchThenEof_whenParse_thenFailWithMissingLongNameValueException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("--option"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * missing value in short name bundle
   */
  @Test(expected = OptionValueMissingSyntaxException.class)
  public void givenBundleNeedingValueThenEof_whenParse_thenFailWithMissingShortNameValueException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("-of"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unknown long name
   */
  @Test(expected = UnrecognizedSwitchSyntaxException.class)
  public void givenArgsWithUnknownLongSwitch_whenParse_thenFailWithUnrecognizedLongNameException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("--foobar"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unknown short name
   */
  @Test(expected = UnrecognizedSwitchSyntaxException.class)
  public void givenArgsWithUnknownShortSwitch_whenParse_thenFailWithUnrecognizedShortNameException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("-z"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * long name connected value for flag
   */
  @Test(expected = FlagValuePresentSyntaxException.class)
  public void givenConnectedValueOnFlagSwitch_whenParse_thenFailWithInvalidLongNameValueException() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(List.of("--flag=foo"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * short flag, long connected option, separator, positional
   */
  @Test
  public void givenShortFlagThenLongConnectedOptionThenSeparatorThenPositional_whenParse_thenSucceed() {
    SingleCommand<Example> cc = (SingleCommand<Example>) Command.scan(Example.class);

    final String alpha = "alpha";
    final String foo = "-foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
      @Override
      public void flag(NameCoordinate name, FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(NameCoordinate name, OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionCoordinate position, PositionalConfigurationParameter property,
          String value) {
        position0.set(value);
      }
    }).parse(asList("-f", "--option=" + alpha, "--", foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }
}
