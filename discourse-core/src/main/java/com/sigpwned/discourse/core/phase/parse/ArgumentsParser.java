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
package com.sigpwned.discourse.core.phase.parse;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.exception.SyntaxException;
import com.sigpwned.discourse.core.phase.parse.exception.FlagValuePresentArgumentException;
import com.sigpwned.discourse.core.phase.parse.exception.NoSuchSwitchArgumentException;
import com.sigpwned.discourse.core.phase.parse.exception.OptionValueMissingArgumentException;
import com.sigpwned.discourse.core.phase.parse.model.SwitchType;
import com.sigpwned.discourse.core.phase.parse.model.coordinate.LongSwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.phase.parse.model.coordinate.PositionArgumentCoordinate;
import com.sigpwned.discourse.core.phase.parse.model.coordinate.ShortSwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.phase.parse.model.coordinate.SwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.phase.parse.model.token.ArgumentToken;
import com.sigpwned.discourse.core.phase.parse.model.token.BundleArgumentToken;
import com.sigpwned.discourse.core.phase.parse.model.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.phase.parse.model.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.phase.parse.model.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.phase.parse.model.token.ShortNameArgumentToken;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>
 * An event parser for command line arguments. Given a set of
 * {@link com.sigpwned.discourse.core.model.coordinate.SwitchNameCoordinate switches} comprising
 * either flags or options, this class will parse a list of command-line arguments and emit events
 * to a {@link Handler} instance corresponding to the arguments being parsed.
 * </p>
 *
 * <p>
 * This class does not check whether the values of the arguments are valid, or that all parameters
 * marked required in the command are given. Those checks are performed elsewhere. This class only
 * checks the syntax of the command line.
 * </p>
 */
public class ArgumentsParser {

  public static interface Handler {

    /**
     * Called when a flag is encountered in the command line. A flag is a boolean-valued switch that
     * is either present or not, as opposed to having an explicit value. For example, {@code -h} and
     * {@code --help} are flags.
     *
     * @param s the coordinate (i.e., switch) of the flag
     */
    default void flag(SwitchNameArgumentCoordinate s) {
    }

    /**
     * Called when an option is encountered in the command line. An option is a switch that has an
     * explicit value. For example, {@code -f file.txt} and {@code --file file.txt} are options.
     *
     * @param s     the coordinate (i.e., switch) of the option
     * @param value the value of the option
     */
    default void option(SwitchNameArgumentCoordinate s, String value) {
    }

    /**
     * Called when a positional argument is encountered in the command line. A positional is a
     * parameter that is not preceded by a switch and is instead identified by its position in the
     * command line. For example, in the command {@code cp file1 file2}, {@code file1} and
     * {@code file2} are positionals.
     *
     * @param position the position of the positional argument
     * @param value    the value of the positional
     */
    default void positional(PositionArgumentCoordinate position, String value) {
    }
  }

  private final SwitchClassifier switchClassifier;
  private ListIterator<String> iterator;
  private Handler handler;

  public ArgumentsParser(SwitchClassifier switchClassifier) {
    this.switchClassifier = requireNonNull(switchClassifier);
  }

  /**
   * Parses the given arguments, emitting events to the instance's {@link Handler handler}.
   *
   * @param args the arguments to parse
   * @throws SyntaxException if the command line could not be parsed
   */
  public void parse(List<String> args, Handler handler) {
    iterator = unmodifiableList(args).listIterator();
    try {
      this.handler = requireNonNull(handler);
      try {
        doParse();
      } finally {
        this.handler = null;
      }
    } finally {
      iterator = null;
    }
  }

  protected void doParse() {
    PositionArgumentCoordinate position = PositionArgumentCoordinate.ZERO;
    boolean positionals = false;
    while (peek() != null) {
      String next = requireNonNull(next());
      if (positionals || !next.startsWith("-")) {
        getHandler().positional(position, next);

        positionals = true;

        position = position.next();
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
        } else if (token instanceof SeparatorArgumentToken) {
          positionals = true;
        }
      }
    }
  }

  private void handleBundle(BundleArgumentToken bundle) {
    for (int index = 0; index < bundle.getShortNames().size(); index++) {
      boolean lastIndex = index == bundle.getShortNames().size() - 1;

      ShortSwitchNameArgumentCoordinate name = ShortSwitchNameArgumentCoordinate.of(
          bundle.getShortNames().get(index));

      SwitchType type = getSwitchClassifier().classifySwitch(name)
          .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

      switch (type) {
        case FLAG:
          getHandler().flag(name);
          break;
        case OPTION:
          if (!lastIndex) {
            // If this isn't the last index, then we're in the middle of the bundle. Values are not
            // allowed here. So if we need one, we're SOL. That's an error.
            throw new OptionValueMissingArgumentException(name);
          }
          if (peek() == null) {
            throw new OptionValueMissingArgumentException(name);
          }
          String value = next();
          getHandler().option(name, value);
          break;
      }
    }
  }

  private void handleShortName(ShortNameArgumentToken token) {
    ShortSwitchNameArgumentCoordinate name = ShortSwitchNameArgumentCoordinate.of(token.getShortName());

    SwitchType type = getSwitchClassifier().classifySwitch(name)
        .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

    switch (type) {
      case FLAG:
        getHandler().flag(name);
        break;
      case OPTION:
        if (peek() == null) {
          throw new OptionValueMissingArgumentException(name);
        }
        String value = next();
        getHandler().option(name, value);
        break;
    }
  }

  private void handleLongName(LongNameArgumentToken token) {
    LongSwitchNameArgumentCoordinate name = LongSwitchNameArgumentCoordinate.of(token.getLongName());

    SwitchType type = getSwitchClassifier().classifySwitch(name)
        .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

    switch (type) {
      case FLAG:
        getHandler().flag(name);
        break;
      case OPTION:
        if (peek() == null) {
          throw new OptionValueMissingArgumentException(name);
        }
        String value = next();
        getHandler().option(name, value);
        break;
    }
  }

  private void handleLongNameValue(LongNameValueArgumentToken token) {
    LongSwitchNameArgumentCoordinate name = LongSwitchNameArgumentCoordinate.fromString(token.getLongName());

    SwitchType type = getSwitchClassifier().classifySwitch(name)
        .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

    String value = token.getValue();

    switch (type) {
      case FLAG:
        throw new FlagValuePresentArgumentException(name);
      case OPTION:
        getHandler().option(name, value);
        break;
    }
  }

  private String peek() {
    if (iterator == null) {
      throw new IllegalStateException("no iterator");
    }
    if (iterator.hasNext()) {
      String result = iterator.next();
      iterator.previous();
      return result;
    } else {
      return null;
    }
  }

  private String next() {
    if (iterator == null) {
      throw new IllegalStateException("no iterator");
    }
    return iterator.hasNext() ? iterator.next() : null;
  }

  /**
   * @return the handler
   */
  private Handler getHandler() {
    if (handler == null) {
      throw new IllegalStateException("no handler");
    }
    return handler;
  }

  private SwitchClassifier getSwitchClassifier() {
    return switchClassifier;
  }
}
