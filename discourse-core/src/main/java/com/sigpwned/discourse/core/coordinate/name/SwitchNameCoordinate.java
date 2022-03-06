package com.sigpwned.discourse.core.coordinate.name;

import java.util.Objects;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;

public abstract class SwitchNameCoordinate extends NameCoordinate {
  public static SwitchNameCoordinate fromSwitchString(String s) {
    if (s.startsWith(LongSwitchNameCoordinate.PREFIX)) {
      return new LongSwitchNameCoordinate(s.substring(LongSwitchNameCoordinate.PREFIX.length(), s.length()));
    } else if (s.startsWith(ShortSwitchNameCoordinate.PREFIX)) {
      return new LongSwitchNameCoordinate(s.substring(ShortSwitchNameCoordinate.PREFIX.length(), s.length()));
    } else {
      throw new IllegalArgumentException("invalid switch string: " + s);
    }
  }

  public static enum Style {
    SHORT, LONG;
  }

  private final Style style;

  protected SwitchNameCoordinate(Style style, String text) {
    super(Type.SWITCH, text);
    this.style = style;
  }

  /**
   * @return the style
   */
  public Style getStyle() {
    return style;
  }

  public ShortSwitchNameCoordinate asShortSwitchName() {
    return (ShortSwitchNameCoordinate) this;
  }

  public LongSwitchNameCoordinate asLongSwitchName() {
    return (LongSwitchNameCoordinate) this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(style);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    SwitchNameCoordinate other = (SwitchNameCoordinate) obj;
    return style == other.style;
  }

  public abstract String toSwitchString();
}
