package com.sigpwned.discourse.core;

import java.io.Serializable;
import java.util.Objects;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.util.Generated;

public abstract class Coordinate implements Serializable {
  public static enum Family {
    NAME, POSITION;
  }
  
  private final Family family;

  protected Coordinate(Family family) {
    this.family = family;
  }

  /**
   * @return the flavor
   */
  public Family getFamily() {
    return family;
  }
  
  public NameCoordinate asName() {
    return (NameCoordinate) this;
  }
  
  public PositionCoordinate asPosition() {
    return (PositionCoordinate) this;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(family);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Coordinate other = (Coordinate) obj;
    return family == other.family;
  }
}
