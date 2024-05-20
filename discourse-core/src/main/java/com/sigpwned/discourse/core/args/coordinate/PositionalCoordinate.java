package com.sigpwned.discourse.core.args.coordinate;

import java.util.Objects;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.args.Coordinate;

public final class PositionalCoordinate extends Coordinate {
  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9]");

  public static final PositionalCoordinate ZERO = new PositionalCoordinate(0);

  public static PositionalCoordinate of(int position) {
    if (position == 0)
      return ZERO;
    return new PositionalCoordinate(position);
  }

  private final int position;

  public PositionalCoordinate(int position) {
    if (position < 0)
      throw new IllegalArgumentException("position must not be negative");
    this.position = position;
  }

  public int getPosition() {
    return position;
  }

  public PositionalCoordinate next() {
    return new PositionalCoordinate(getPosition() + 1);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PositionalCoordinate other = (PositionalCoordinate) obj;
    return position == other.position;
  }

  @Override
  public String toString() {
    return "PositionalCoordinate[" + position + "]";
  }
}
