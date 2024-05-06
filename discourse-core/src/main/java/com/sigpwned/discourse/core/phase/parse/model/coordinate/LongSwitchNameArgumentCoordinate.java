package com.sigpwned.discourse.core.phase.parse.model.coordinate;

import java.util.regex.Pattern;

public final class LongSwitchNameArgumentCoordinate extends SwitchNameArgumentCoordinate {

  public static final String PREFIX = "--";

  public static LongSwitchNameArgumentCoordinate fromString(String s) {
    if (s == null) {
      throw new NullPointerException();
    }
    if (!s.startsWith(PREFIX)) {
      throw new IllegalArgumentException("string must start with prefix %s".formatted(PREFIX));
    }
    return new LongSwitchNameArgumentCoordinate(s.substring(PREFIX.length()));
  }

  public static LongSwitchNameArgumentCoordinate of(String text) {
    return new LongSwitchNameArgumentCoordinate(text);
  }

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9_.]*");

  public LongSwitchNameArgumentCoordinate(String text) {
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
