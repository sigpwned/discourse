package com.sigpwned.discourse.core.phase.parse.model.coordinate;

import static java.util.Objects.requireNonNull;

public sealed abstract class SwitchNameArgumentCoordinate extends ArgumentCoordinate permits
    LongSwitchNameArgumentCoordinate, ShortSwitchNameArgumentCoordinate {

  public static SwitchNameArgumentCoordinate fromString(String s) {
    if (s == null) {
      throw new NullPointerException();
    }
    if (s.startsWith(LongSwitchNameArgumentCoordinate.PREFIX)) {
      return LongSwitchNameArgumentCoordinate.fromString(s);
    }
    if (s.startsWith(ShortSwitchNameArgumentCoordinate.PREFIX)) {
      return ShortSwitchNameArgumentCoordinate.fromString(s);
    }
    throw new IllegalArgumentException(
        "string must start with prefix %s or %s".formatted(LongSwitchNameArgumentCoordinate.PREFIX,
            ShortSwitchNameArgumentCoordinate.PREFIX));
  }

  private final String text;

  protected SwitchNameArgumentCoordinate(String text) {
    this.text = requireNonNull(text);
  }

  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
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
    SwitchNameArgumentCoordinate other = (SwitchNameArgumentCoordinate) obj;
    return toString().equals(other.toString());
  }
}
