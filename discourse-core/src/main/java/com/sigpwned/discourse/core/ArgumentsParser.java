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

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.syntax.FlagValuePresentSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.OptionValueMissingSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedSwitchSyntaxException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.token.ArgumentToken;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;
import java.util.List;
import java.util.ListIterator;

/**
 * A parser for command line arguments.
 */
public class ArgumentsParser {

  public static interface Handler {

    default void flag(NameCoordinate coordinate, FlagConfigurationParameter property) {
    }

    default void option(NameCoordinate coordinate, OptionConfigurationParameter property,
        String value) {
    }

    default void positional(PositionCoordinate coordinate,
        PositionalConfigurationParameter property, String value) {
    }
  }

  private final SingleCommand<?> command;
  private final Handler handler;
  private ListIterator<String> iterator;

  public ArgumentsParser(SingleCommand<?> command, Handler handler) {
    this.command = requireNonNull(command);
    this.handler = requireNonNull(handler);
  }

  /**
   * Parses the given arguments, emitting events to the instance's {@link Handler handler}.
   *
   * @param args the arguments to parse
   * @throws SyntaxException if the command line could not be parsed
   */
  public void parse(List<String> args) {
    iterator = unmodifiableList(args).listIterator();
    try {
      parse();
    } finally {
      iterator = null;
    }
  }

  private void parse() {
    PositionCoordinate position = PositionCoordinate.ZERO;
    boolean positionals = false;
    while (peek() != null) {
      String next = next();
      if (next == null) {
        throw new AssertionError("no next token");
      }
      if (positionals || !next.startsWith("-")) {
        final int index = position.getIndex();

        PositionalConfigurationParameter positionalProperty = (PositionalConfigurationParameter) getParameterResolver().resolveConfigurationParameter(
            position).orElseThrow(() -> new MissingPositionConfigurationException(index));

        getHandler().positional(position, positionalProperty, next);

        positionals = true;

        if (!positionalProperty.isCollection()) {
          position = position.next();
        }
      } else {
        ArgumentToken token = ArgumentToken.fromString(next);
        if (token instanceof BundleArgumentToken bundle) {
          handleBundle(bundle);
        } else if (token instanceof LongNameArgumentToken longName) {
          handleLongName(longName);
        } else if (token instanceof LongNameValueArgumentToken longNameValue) {
          handleLongNameValue(longNameValue);
        } else if (token instanceof ShortNameArgumentToken shortName) {
          handleShortName(shortName);
        } else if (token instanceof SeparatorArgumentToken separator) {
          positionals = true;
        }
      }
    }
  }

  private void handleBundle(BundleArgumentToken bundle) {
    for (int index = 0; index < bundle.getShortNames().size(); index++) {
      boolean lastIndex = index == bundle.getShortNames().size() - 1;

      ShortSwitchNameCoordinate shortName = ShortSwitchNameCoordinate.fromString(
          bundle.getShortNames().get(index));

      ConfigurationParameter shortNameProperty = getParameterResolver().resolveConfigurationParameter(
              shortName)
          .orElseThrow(() -> new UnrecognizedSwitchSyntaxException(getCommand(), shortName));

      if (shortNameProperty.isValued()) {
        OptionConfigurationParameter optionProperty = (OptionConfigurationParameter) shortNameProperty;
        if (!lastIndex) {
          // If this isn't the last index, then we're in the middle of the bundle. Values are not
          // allowed here. So if we need one, we're SOL. That's an error.
          throw new OptionValueMissingSyntaxException(getCommand(), shortNameProperty.getName(),
              shortName);
        }
        if (peek() == null) {
          throw new OptionValueMissingSyntaxException(getCommand(), shortNameProperty.getName(),
              shortName);
        }
        String value = next();
        getHandler().option(shortName, optionProperty, value);
      } else {
        FlagConfigurationParameter flagProperty = (FlagConfigurationParameter) shortNameProperty;
        getHandler().flag(shortName, flagProperty);
      }
    }
  }

  private void handleShortName(ShortNameArgumentToken token) {
    ShortSwitchNameCoordinate shortName = ShortSwitchNameCoordinate.fromString(
        token.getShortName());

    ConfigurationParameter shortNameProperty = getParameterResolver().resolveConfigurationParameter(
            shortName)
        .orElseThrow(() -> new UnrecognizedSwitchSyntaxException(getCommand(), shortName));

    if (shortNameProperty.isValued()) {
      OptionConfigurationParameter optionProperty = (OptionConfigurationParameter) shortNameProperty;
      if (peek() == null) {
        throw new OptionValueMissingSyntaxException(getCommand(), optionProperty.getName(),
            shortName);
      }
      String value = next();
      getHandler().option(shortName, optionProperty, value);
    } else {
      FlagConfigurationParameter flagProperty = (FlagConfigurationParameter) shortNameProperty;
      getHandler().flag(shortName, flagProperty);
    }
  }

  private void handleLongName(LongNameArgumentToken token) {
    LongSwitchNameCoordinate longName = LongSwitchNameCoordinate.fromString(token.getLongName());

    ConfigurationParameter longNameProperty = getParameterResolver().resolveConfigurationParameter(
        longName).orElseThrow(() -> new UnrecognizedSwitchSyntaxException(getCommand(), longName));

    if (longNameProperty.isValued()) {
      OptionConfigurationParameter optionProperty = (OptionConfigurationParameter) longNameProperty;
      if (peek() == null) {
        throw new OptionValueMissingSyntaxException(getCommand(), optionProperty.getName(),
            longName);
      }
      String value = next();
      getHandler().option(longName, optionProperty, value);
    } else {
      FlagConfigurationParameter flagProperty = (FlagConfigurationParameter) longNameProperty;
      getHandler().flag(longName, flagProperty);
    }
  }

  private void handleLongNameValue(LongNameValueArgumentToken token) {
    LongSwitchNameCoordinate longName = LongSwitchNameCoordinate.fromString(token.getLongName());
    String value = token.getValue();

    ConfigurationParameter longNameValueProperty = getParameterResolver().resolveConfigurationParameter(
        longName).orElseThrow(() -> new UnrecognizedSwitchSyntaxException(getCommand(), longName));

    if (longNameValueProperty.isValued()) {
      OptionConfigurationParameter optionProperty = (OptionConfigurationParameter) longNameValueProperty;
      getHandler().option(longName, optionProperty, value);
    } else {
      throw new FlagValuePresentSyntaxException(getCommand(), longNameValueProperty.getName(),
          longName);
    }
  }

  private String peek() {
    if (iterator.hasNext()) {
      String result = iterator.next();
      iterator.previous();
      return result;
    } else {
      return null;
    }
  }

  private String next() {
    return iterator.hasNext() ? iterator.next() : null;
  }

  /**
   * @return the configurationClass
   */
  private ConfigurationParameterResolver getParameterResolver() {
    return getCommand()::findParameter;
  }

  /**
   * @return the handler
   */
  private Handler getHandler() {
    return handler;
  }

  /**
   * @return the command being parsed
   */
  private SingleCommand<?> getCommand() {
    return command;
  }
}
