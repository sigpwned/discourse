package com.sigpwned.discourse.core.invocation.phase.parse.impl.args.model.coordinate;

import java.util.regex.Pattern;

public final class ShortSwitchNameArgumentCoordinate extends SwitchNameArgumentCoordinate {

  public static final String PREFIX = "-";

  public static ShortSwitchNameArgumentCoordinate fromString(String s) {
    if (s == null) {
      throw new NullPointerException();
    }
    if (!s.startsWith(PREFIX)) {
      throw new IllegalArgumentException("string must start with prefix %s".formatted(PREFIX));
    }
    return new ShortSwitchNameArgumentCoordinate(s.substring(PREFIX.length()));
  }

  public static ShortSwitchNameArgumentCoordinate of(String text) {
    return new ShortSwitchNameArgumentCoordinate(text);
  }

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9]+");

  public ShortSwitchNameArgumentCoordinate(String text) {
    super(text);
    if (!PATTERN.matcher(text).matches()) {
      throw new IllegalArgumentException("text must match pattern %s".formatted(PATTERN));
    }
  }

  @Override
  public String toString() {
    return PREFIX + getText();
  }
}
