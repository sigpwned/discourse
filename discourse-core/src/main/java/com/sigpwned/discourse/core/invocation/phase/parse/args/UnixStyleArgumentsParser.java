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
package com.sigpwned.discourse.core.invocation.phase.parse.args;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.invocation.phase.parse.ArgumentsParser;
import com.sigpwned.discourse.core.invocation.phase.parse.args.exception.FlagValuePresentArgumentException;
import com.sigpwned.discourse.core.invocation.phase.parse.args.exception.NoSuchSwitchArgumentException;
import com.sigpwned.discourse.core.invocation.phase.parse.args.exception.OptionValueMissingArgumentException;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.coordinate.LongSwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.coordinate.PositionArgumentCoordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.coordinate.ShortSwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.coordinate.SwitchNameArgumentCoordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.token.ArgumentToken;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.token.BundleArgumentToken;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.invocation.phase.parse.args.model.token.ShortNameArgumentToken;

/**
 * <p>
 * A parser for Unix-style command-line arguments.
 * </p>
 * 
 * <p>
 * This parser recognizes the following syntax:
 * </p>
 * 
 * <ul>
 * <li>Long flag -- A long flag is a long switch with a boolean value that is determined by the
 * presence or absence of the switch. For example, the common long help argument {@code --help} is a
 * long flag.</li>
 * <li>Short flag -- A short flag is a short switch with a boolean value that is determined by the
 * presence or absence of the switch. For example, the common short help argument {@code -h} is a
 * short flag.</li>
 * <li>Long option with detached value -- A long option with a detached value is a long switch
 * followed by a corresponding value. For example, the fragment {@code --file file.txt} is a long
 * option ({@code --file}) with a detached value ({@code file.txt}).</li>
 * <li>Long option with attached value -- A long option with attached value is a long switch that is
 * followed by an equals sign and a value. For example, the fragment {@code --file=file.txt} is a
 * long option ({@code --file}) with attached value ({@code file.txt}).</li>
 * <li>Short option with value -- A short option with a value is a short switch followed by a
 * corresponding value. For example, the fragment {@code -f file.txt} is a short option ({@code -f})
 * with a value ({@code file.txt}).</li>
 * <li>Bundle -- A bundle is a group of short switches that are bundled together without spaces. For
 * example, the fragment {@code -abc} is a bundle, which is equivalent to {@code -a -b -c}. Note
 * that switches "internal" to the bundle (i.e., {@code a} and {@code b} in this example) cannot
 * take values, so must be flags. The final switch in a bundle may take a value, but does not have
 * to.</li>
 * <li>Separator -- A separator is a double dash that indicates that all subsequent tokens are
 * positional arguments. For example, the fragment {@code -f -- -g} would parse as the flag
 * {@code -f} followed by the positional {@code -g}. This is useful when generating arguments
 * programmatically, or when positional arguments may look like switches.</li>
 * <li>Positional arguments -- A positional argument is a value that appears after all switches have
 * been given. A separator may be used to indicate that all subsequent tokens are positional.</li>
 * </ul>
 */
public class UnixStyleArgumentsParser implements ArgumentsParser {
  public static final Pattern LONG_SWITCH_PATTERN = ArgumentToken.LONG_NAME;

  public static final Pattern SHORT_SWITCH_PATTERN = ArgumentToken.SHORT_NAME;

  public static final Pattern POSITIONAL_PATTERN = Pattern.compile("@[0-9]+");

  public static final String FLAG_TYPE = "flag";

  public static final String OPTION_TYPE = "option";

  private static interface Handler {

    void flag(SwitchNameArgumentCoordinate name);

    void option(SwitchNameArgumentCoordinate name, String value);

    void positional(PositionArgumentCoordinate position, String value);
  }

  private Map<String, String> vocabulary;
  private ListIterator<String> iterator;
  private Handler handler;

  /**
   * <p>
   * Parses the given arguments using the given vocabulary, returning the result as a list of
   * coordinate pairs.
   * </p>
   * 
   * <p>
   * In this implementation, the vocabulary should be a map of switch names to their types. Each
   * switch name should be a string representing either a {@link #LONG_SWITCH_PATTERN long switch}
   * or a {@link #SHORT_SWITCH_PATTERN short switch}, and each type should be one of
   * {@value #FLAG_TYPE}, {@value #OPTION_TYPE}. The parser will use this to determine how to
   * interpret the arguments, namely whether a switch takes an option or not. For example, a valid
   * vocabulary might look like
   * <code>Map.of("--help", {@value #FLAG_TYPE}, "--file", {@value #OPTION_TYPE})</code>.
   * </p>
   * 
   * <p>
   * In this implementation, the result will be a list of key-value pairs representing the parsed
   * arguments. The keys will be a description of the location of the value within the arguments,
   * and the values will be the parsed values as they appeared in the user arguments. For example,
   * the arguments {@code --help --file file.txt hello} would be parsed as a list containing the
   * entries {@code [("--help", "true"), ("--file", "file.txt"), ("@0", "hello")]}. The keys are
   * generated as follows: long switches are represented as {@code --name}, short switches are
   * represented as {@code -n}, and positional arguments are represented as {@code @n}, where
   * {@code n} is the zero-based position of the argument in the user arguments.
   * </p>
   * 
   *
   * @param vocabulary the vocabulary to use
   * @param args the arguments to parse
   * @return the parsed arguments
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

      ShortSwitchNameArgumentCoordinate name =
          ShortSwitchNameArgumentCoordinate.of(bundle.getShortNames().get(index));

      String type = Optional.ofNullable(getVocabulary().get(name.toString()))
          .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

      switch (type) {
        case FLAG_TYPE:
          getHandler().flag(name);
          break;
        case OPTION_TYPE:
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
    ShortSwitchNameArgumentCoordinate name =
        ShortSwitchNameArgumentCoordinate.of(token.getShortName());

    String type = Optional.ofNullable(getVocabulary().get(name.toString()))
        .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

    switch (type) {
      case FLAG_TYPE:
        getHandler().flag(name);
        break;
      case OPTION_TYPE:
        if (peek() == null) {
          throw new OptionValueMissingArgumentException(name);
        }
        String value = next();
        getHandler().option(name, value);
        break;
    }
  }

  private void handleLongName(LongNameArgumentToken token) {
    LongSwitchNameArgumentCoordinate name =
        LongSwitchNameArgumentCoordinate.of(token.getLongName());

    String type = Optional.ofNullable(getVocabulary().get(name.toString()))
        .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

    switch (type) {
      case FLAG_TYPE:
        getHandler().flag(name);
        break;
      case OPTION_TYPE:
        if (peek() == null) {
          throw new OptionValueMissingArgumentException(name);
        }
        String value = next();
        getHandler().option(name, value);
        break;
    }
  }

  private void handleLongNameValue(LongNameValueArgumentToken token) {
    LongSwitchNameArgumentCoordinate name =
        LongSwitchNameArgumentCoordinate.fromString(token.getLongName());

    String type = Optional.ofNullable(getVocabulary().get(name.toString()))
        .orElseThrow(() -> new NoSuchSwitchArgumentException(name));

    String value = token.getValue();

    switch (type) {
      case FLAG_TYPE:
        throw new FlagValuePresentArgumentException(name);
      case OPTION_TYPE:
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
