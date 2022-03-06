package com.sigpwned.discourse.core.coordinate.name.switches;

import java.util.regex.Pattern;
import com.sigpwned.discourse.core.coordinate.name.SwitchNameCoordinate;

public class ShortSwitchNameCoordinate extends SwitchNameCoordinate {
  public static final String PREFIX = "-";

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9]");

  public static ShortSwitchNameCoordinate fromString(String s) {
    return new ShortSwitchNameCoordinate(s);
  }

  public ShortSwitchNameCoordinate(String text) {
    super(Style.SHORT, text);
    if (!PATTERN.matcher(text).matches())
      throw new IllegalArgumentException("invalid short name: " + text);
  }

  @Override
  public String toSwitchString() {
    return PREFIX + toString();
  }
}
