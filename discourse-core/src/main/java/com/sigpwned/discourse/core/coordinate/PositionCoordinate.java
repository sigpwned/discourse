package com.sigpwned.discourse.core.coordinate;

import java.util.Objects;
import com.sigpwned.discourse.core.Coordinate;

public class PositionCoordinate extends Coordinate implements Comparable<PositionCoordinate> {
  public static final PositionCoordinate ZERO = new PositionCoordinate(0);

  public static PositionCoordinate of(int index) {
    if (index == 0)
      return ZERO;
    return new PositionCoordinate(index);
  }

  private final int index;

  public PositionCoordinate(int index) {
    super(Flavor.POSITION);
    if (index < 0)
      throw new IllegalArgumentException("index is negative");
    this.index = index;
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  public PositionCoordinate next() {
    return of(getIndex() + 1);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PositionCoordinate other = (PositionCoordinate) obj;
    return index == other.index;
  }

  @Override
  public String toString() {
    return "Position [index=" + index + "]";
  }

  @Override
  public int compareTo(PositionCoordinate that) {
    return this.getIndex() - that.getIndex();
  }
}
