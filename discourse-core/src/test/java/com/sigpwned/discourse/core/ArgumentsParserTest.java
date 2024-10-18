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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.exception.syntax.InvalidLongNameValueSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.MissingLongNameValueSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.MissingShortNameValueSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedLongNameSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedShortNameSyntaxException;
import com.sigpwned.discourse.core.module.DefaultModule;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;

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
  
  public SerializationContext serializationContext;
  public SinkContext sinkContext;
  
  @Before
  public void setupArgumentsParserTest() {
    serializationContext = new SerializationContext();
    sinkContext = new SinkContext();
    
    Module module=new DefaultModule();
    
    module.register(serializationContext);
    module.register(sinkContext);
  }
  
  @After
  public void cleanupArgumentsParserTest() {
    serializationContext = null;
    sinkContext = null;
  }

  /**
   * short flag, long disconnected option, positional
   */
  @Test
  public void test1() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
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
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
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
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
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
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    new ArgumentsParser(cc, new ArgumentsParser.Handler() {}).parse(asList("-x"));
  }

  /**
   * short flag, long connected option, positional
   */
  @Test
  public void test5() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-f", "--option=" + alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * two bundled short flags, option, positional
   */
  @Test
  public void test6() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-fo", alpha, foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unexpected eof looking for option value after short name
   */
  @Test(expected = MissingShortNameValueSyntaxException.class)
  public void test7() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-o"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unexpected eof looking for option value after long name
   */
  @Test(expected = MissingLongNameValueSyntaxException.class)
  public void test8() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("--option"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * missing value in short name bundle
   */
  @Test(expected = MissingShortNameValueSyntaxException.class)
  public void test9() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-of"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unexpected eof looking for option value after short name in bundle
   */
  @Test(expected = MissingShortNameValueSyntaxException.class)
  public void test10() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-fo"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unknown long name
   */
  @Test(expected = UnrecognizedLongNameSyntaxException.class)
  public void test11() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("--foobar"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * unknown short name
   */
  @Test(expected = UnrecognizedShortNameSyntaxException.class)
  public void test12() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-z"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * long name connected value for flag
   */
  @Test(expected = InvalidLongNameValueSyntaxException.class)
  public void test13() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {

      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("--flag=foo"));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }

  /**
   * short flag, long connected option, separator, positional
   */
  @Test
  public void test14() {
    ConfigurationClass cc =
        ConfigurationClass.scan(sinkContext, serializationContext, Example.class);

    final String alpha = "alpha";
    final String foo = "foo";

    final AtomicBoolean flag = new AtomicBoolean(false);
    final AtomicReference<String> option = new AtomicReference<>();
    final AtomicReference<String> position0 = new AtomicReference<>();
    new ArgumentsParser(cc, new ArgumentsParser.Handler() {
      @Override
      public void flag(FlagConfigurationParameter property) {
        flag.set(true);
      }

      @Override
      public void option(OptionConfigurationParameter property, String value) {
        option.set(value);
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String value) {
        position0.set(value);
      }
    }).parse(asList("-f", "--option=" + alpha, "--", foo));

    assertThat(flag.get(), is(true));
    assertThat(option.get(), is(alpha));
    assertThat(position0.get(), is(foo));
  }
}
