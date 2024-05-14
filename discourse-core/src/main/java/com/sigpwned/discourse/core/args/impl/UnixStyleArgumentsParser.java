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
package com.sigpwned.discourse.core.args.impl;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.args.ArgumentsParser;
import com.sigpwned.discourse.core.args.exception.FlagValuePresentArgumentException;
import com.sigpwned.discourse.core.args.exception.NoSuchSwitchArgumentException;
import com.sigpwned.discourse.core.args.exception.OptionValueMissingArgumentException;
import com.sigpwned.discourse.core.args.impl.model.coordinate.LongSwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.args.impl.model.coordinate.PositionArgumentCoordinate;
import com.sigpwned.discourse.core.args.impl.model.coordinate.ShortSwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.args.impl.model.coordinate.SwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.args.impl.model.token.ArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.BundleArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.args.impl.model.token.ShortNameArgumentToken;
import com.sigpwned.discourse.core.exception.SyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A parser for Unix-style command-line arguments.
 * </p>
 *
 * <p>
 * This parser supports the following vocabulary types:
 * </p>
 *
 * <ul>
 *   <li>
 *     {@value #FLAG}: A flag argument. This is a boolean-valued switch whose value is given by
 *     either its presence or absence. For example, the standard Unix help switches {@code -h} and
 *     {@code --help} are flags.
 *   </li>
 *   <li>
 *     {@value #OPTION}: An option argument. This is a switch that takes an explicit value. For
 *     example, in the command {@code -f file.txt}, the switch {@code -f} is an option.
 *   </li>
 * </ul>
 *
 * <p>
 * Therefore, an example vocabulary value might look like the following:
 * </p>
 *
 * <pre>
 *   Map.of(
 *     "-h", FLAG,
 *     "--help", FLAG,
 *     "-f", OPTION)
 * </pre>
 */
public class UnixStyleArgumentsParser implements ArgumentsParser {

  public static final String FLAG = "flag";

  public static final String OPTION = "option";

  private static interface Handler {

    void flag(SwitchNameArgumentCoordinate name);

    void option(SwitchNameArgumentCoordinate name, String value);

    void positional(PositionArgumentCoordinate position, String value);
  }

  private Map<String, String> vocabulary;
  private ListIterator<String> iterator;
  private Handler handler;

  /**
   * Parses the given arguments, emitting events to the instance's {@link Handler handler}.
   *
   * @param args the arguments to parse
   * @throws SyntaxException if the resolvedCommand line could not be parsed
   */
  public List<Map.Entry<String, String>> parse(Map<String, String> vocabulary, List<String> args) {
    final List<Map.Entry<String, String>> result = new ArrayList<>();

    this.vocabulary = requireNonNull(vocabulary);
    this.handler = new Handler() {
      @Override
      public void flag(SwitchNameArgumentCoordinate name) {
        add(name.toString(), "true");
      }

      @Override
      public void option(SwitchNameArgumentCoordinate name, String value) {
        add(name.toString(), value);
      }

      @Override
      public void positional(PositionArgumentCoordinate position, String value) {
        add(position.toString(), value);
      }

      private void add(String name, String value) {
        result.add(Map.entry(name, value));
      }
    };
    this.iterator = unmodifiableList(args).listIterator();
    try {
      doParse();
    } finally {
      this.iterator = null;
      this.handler = null;
      this.vocabulary = null;
    }

    return unmodifiableList(result);
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

      String type = Optional.ofNullable(getVocabulary().get(name.toString()))
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
    ShortSwitchNameArgumentCoordinate name = ShortSwitchNameArgumentCoordinate.of(
        token.getShortName());

    String type = Optional.ofNullable(getVocabulary().get(name.toString()))
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
    LongSwitchNameArgumentCoordinate name = LongSwitchNameArgumentCoordinate.of(
        token.getLongName());

    String type = Optional.ofNullable(getVocabulary().get(name.toString()))
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
    LongSwitchNameArgumentCoordinate name = LongSwitchNameArgumentCoordinate.fromString(
        token.getLongName());

    String type = Optional.ofNullable(getVocabulary().get(name.toString()))
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

  private Map<String, String> getVocabulary() {
    return vocabulary;
  }
}
