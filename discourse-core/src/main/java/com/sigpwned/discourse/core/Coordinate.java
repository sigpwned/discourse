package com.sigpwned.discourse.core;

import java.util.Objects;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;

public abstract class Coordinate {
  public static enum Flavor {
    NAME, POSITION;
  }
  
  private final Flavor flavor;

  public Coordinate(Flavor flavor) {
    this.flavor = flavor;
  }

  /**
   * @return the flavor
   */
  public Flavor getFlavor() {
    return flavor;
  }
  
  public NameCoordinate asName() {
    return (NameCoordinate) this;
  }
  
  public PositionCoordinate asPosition() {
    return (PositionCoordinate) this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(flavor);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Coordinate other = (Coordinate) obj;
    return flavor == other.flavor;
  }
}
