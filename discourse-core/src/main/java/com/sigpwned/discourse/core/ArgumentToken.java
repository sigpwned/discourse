package com.sigpwned.discourse.core;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.EofArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;
import com.sigpwned.discourse.core.token.ValueArgumentToken;
import com.sigpwned.discourse.core.util.Generated;

public abstract class ArgumentToken {
  public static enum Type {
    BUNDLE, SHORT_NAME, LONG_NAME, LONG_NAME_VALUE, VALUE, SEPARATOR, EOF;
  }
  
  public static final Pattern SEPARATOR=Pattern.compile("--");
  
  public static final Pattern LONG_NAME_PREFIX = Pattern.compile("--");

  public static final Pattern LONG_NAME_VALUE_SEPARATOR = Pattern.compile("=");

  public static final Pattern SHORT_NAME_PREFIX = Pattern.compile("-");

  public static final Pattern BUNDLE = Pattern.compile(format("%s(%s{2,})",
      SHORT_NAME_PREFIX.pattern(), ShortSwitchNameCoordinate.PATTERN.pattern()));

  public static final Pattern SHORT_NAME = Pattern.compile(
      format("%s(%s)", SHORT_NAME_PREFIX.pattern(), ShortSwitchNameCoordinate.PATTERN.pattern()));

  public static final Pattern LONG_NAME = Pattern.compile(
      format("%s(%s)", LONG_NAME_PREFIX.pattern(), LongSwitchNameCoordinate.PATTERN.pattern()));

  public static final Pattern LONG_NAME_VALUE =
      Pattern.compile(format("%s(%s)%s(%s)", LONG_NAME_PREFIX.pattern(),
          LongSwitchNameCoordinate.PATTERN.pattern(), LONG_NAME_VALUE_SEPARATOR.pattern(), ".*"));  

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
      return new BundleArgumentToken(s, IntStream.range(0, bundle.length())
          .mapToObj(i -> bundle.substring(i, i + 1)).collect(toList()));
    } else if (SHORT_NAME.matcher(s).matches()) {
      Matcher m = SHORT_NAME.matcher(s);
      m.matches();
      String shortName = m.group(1);
      return new ShortNameArgumentToken(s, shortName);
    } else {
      throw new IllegalArgumentException("unrecognized token " + s);
    }
  }

  private final Type type;
  private final String text;

  public ArgumentToken(Type type, String text) {
    this.type = type;
    this.text = text;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
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
    return Objects.hash(text, type);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ArgumentToken other = (ArgumentToken) obj;
    return Objects.equals(text, other.text) && type == other.type;
  }

  @Override
  public String toString() {
    return getText();
  }
}
