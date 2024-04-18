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
package com.sigpwned.discourse.core.token;

import static java.lang.String.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * A token used during the parsing of command-line arguments.
 */
public abstract sealed class ArgumentToken permits BundleArgumentToken, EofArgumentToken,
    LongNameArgumentToken, LongNameValueArgumentToken, SeparatorArgumentToken,
    ShortNameArgumentToken, ValueArgumentToken {

  public static final Pattern SEPARATOR = Pattern.compile("--");

  public static final Pattern LONG_NAME_PREFIX = Pattern.compile("--");

  public static final Pattern LONG_NAME_VALUE_SEPARATOR = Pattern.compile("=");

  public static final Pattern SHORT_NAME_PREFIX = Pattern.compile("-");

  public static final Pattern BUNDLE = Pattern.compile(
      format("%s(%s{2,})", SHORT_NAME_PREFIX.pattern(),
          ShortSwitchNameCoordinate.PATTERN.pattern()));

  public static final Pattern SHORT_NAME = Pattern.compile(
      format("%s(%s)", SHORT_NAME_PREFIX.pattern(), ShortSwitchNameCoordinate.PATTERN.pattern()));

  public static final Pattern LONG_NAME = Pattern.compile(
      format("%s(%s)", LONG_NAME_PREFIX.pattern(), LongSwitchNameCoordinate.PATTERN.pattern()));

  public static final Pattern LONG_NAME_VALUE = Pattern.compile(
      format("%s(%s)%s(%s)", LONG_NAME_PREFIX.pattern(), LongSwitchNameCoordinate.PATTERN.pattern(),
          LONG_NAME_VALUE_SEPARATOR.pattern(), ".*"));

  public static ArgumentToken fromString(String s) {
    if (s == null) {
      return EofArgumentToken.INSTANCE;
    } else if (SEPARATOR.matcher(s).matches()) {
      return SeparatorArgumentToken.INSTANCE;
    } else if (LONG_NAME_VALUE.matcher(s).matches()) {
      Matcher m = LONG_NAME_VALUE.matcher(s);
      m.matches();
      String longName = m.group(1);
      String value = m.group(2);
      return new LongNameValueArgumentToken(s, longName, value);
    } else if (LONG_NAME.matcher(s).matches()) {
      Matcher m = LONG_NAME.matcher(s);
      m.matches();
      String longName = m.group(1);
      return new LongNameArgumentToken(s, longName);
    } else if (BUNDLE.matcher(s).matches()) {
      Matcher m = BUNDLE.matcher(s);
      m.matches();
      String bundle = m.group(1);
      return new BundleArgumentToken(s,
          IntStream.range(0, bundle.length()).mapToObj(i -> bundle.substring(i, i + 1))
              .collect(toList()));
    } else if (SHORT_NAME.matcher(s).matches()) {
      Matcher m = SHORT_NAME.matcher(s);
      m.matches();
      String shortName = m.group(1);
      return new ShortNameArgumentToken(s, shortName);
    } else {
      throw new IllegalArgumentException("unrecognized token " + s);
    }
  }

  private final String text;

  protected ArgumentToken(String text) {
    this.text = requireNonNull(text);
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  public BundleArgumentToken asBundle() {
    return (BundleArgumentToken) this;
  }

  public ShortNameArgumentToken asShortName() {
    return (ShortNameArgumentToken) this;
  }

  public LongNameArgumentToken asLongName() {
    return (LongNameArgumentToken) this;
  }

  public LongNameValueArgumentToken asLongNameValue() {
    return (LongNameValueArgumentToken) this;
  }

  public ValueArgumentToken asValue() {
    return (ValueArgumentToken) this;
  }

  public SeparatorArgumentToken asSeparator() {
    return (SeparatorArgumentToken) this;
  }

  public EofArgumentToken asEof() {
    return (EofArgumentToken) this;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ArgumentToken other = (ArgumentToken) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return getText();
  }
}
