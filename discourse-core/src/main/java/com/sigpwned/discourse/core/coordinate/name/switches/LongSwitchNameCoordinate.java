package com.sigpwned.discourse.core.coordinate.name.switches;

import java.util.regex.Pattern;
import com.sigpwned.discourse.core.coordinate.name.SwitchNameCoordinate;

public class LongSwitchNameCoordinate extends SwitchNameCoordinate {
  public static final String PREFIX = "--";

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9_]*");
  
  public static LongSwitchNameCoordinate fromString(String s) {
    return new LongSwitchNameCoordinate(s);
  }

  public LongSwitchNameCoordinate(String text) {
    super(Style.LONG, text);
    if (!PATTERN.matcher(text).matches())
      throw new IllegalArgumentException("invalid long name: " + text);
  }

  @Override
  public String toSwitchString() {
    return PREFIX + toString();
  }
}
