package com.sigpwned.discourse.core;

import static java.util.stream.Collectors.toList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.token.BundleArgumentToken;
import com.sigpwned.discourse.core.token.EofArgumentToken;
import com.sigpwned.discourse.core.token.LongNameArgumentToken;
import com.sigpwned.discourse.core.token.LongNameValueArgumentToken;
import com.sigpwned.discourse.core.token.SeparatorArgumentToken;
import com.sigpwned.discourse.core.token.ShortNameArgumentToken;
import com.sigpwned.discourse.core.token.ValueArgumentToken;
import com.sigpwned.discourse.core.util.Arguments;

public abstract class ArgumentToken {
  public static enum Type {
    BUNDLE, SHORT_NAME, LONG_NAME, LONG_NAME_VALUE, VALUE, SEPARATOR, EOF;
  }

  public static ArgumentToken fromString(String s) {
    if (s == null) {
      return EofArgumentToken.INSTANCE;
    } else if (Arguments.SEPARATOR.matcher(s).matches()) {
      return SeparatorArgumentToken.INSTANCE;
    } else if (Arguments.LONG_NAME_VALUE.matcher(s).matches()) {
      Matcher m = Arguments.LONG_NAME_VALUE.matcher(s);
      m.matches();
      String longName = m.group(1);
      String value = m.group(2);
      return new LongNameValueArgumentToken(s, longName, value);
    } else if (Arguments.LONG_NAME.matcher(s).matches()) {
      Matcher m = Arguments.LONG_NAME.matcher(s);
      m.matches();
      String longName = m.group(1);
      return new LongNameArgumentToken(s, longName);
    } else if (Arguments.BUNDLE.matcher(s).matches()) {
      Matcher m = Arguments.BUNDLE.matcher(s);
      m.matches();
      String bundle = m.group(1);
      return new BundleArgumentToken(s, IntStream.range(0, bundle.length())
          .mapToObj(i -> bundle.substring(i, i + 1)).collect(toList()));
    } else if (Arguments.SHORT_NAME.matcher(s).matches()) {
      Matcher m = Arguments.SHORT_NAME.matcher(s);
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
  public int hashCode() {
    return Objects.hash(text, type);
  }

  @Override
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

  public String toString() {
    return getText();
  }
}
