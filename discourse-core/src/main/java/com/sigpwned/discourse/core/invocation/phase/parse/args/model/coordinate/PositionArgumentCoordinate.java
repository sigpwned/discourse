package com.sigpwned.discourse.core.invocation.phase.parse.args.model.coordinate;

import java.util.Objects;

public final class PositionArgumentCoordinate extends ArgumentCoordinate
    implements Comparable<PositionArgumentCoordinate> {

  public static final PositionArgumentCoordinate ZERO = new PositionArgumentCoordinate(0);

  public static PositionArgumentCoordinate fromString(String s) {
    if (s == null) {
      throw new NullPointerException();
    }
    if (!s.startsWith("@")) {
      throw new IllegalArgumentException("string must start with prefix @");
    }
    return of(Integer.parseInt(s.substring(1)));
  }

  public static PositionArgumentCoordinate of(int index) {
    if (index == 0) {
      return ZERO;
    }
    return new PositionArgumentCoordinate(index);
  }

  private final int index;

  public PositionArgumentCoordinate(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index must not be negative");
    }
    this.index = index;
  }

  public PositionArgumentCoordinate next() {
    return new PositionArgumentCoordinate(index + 1);
  }

  public int getIndex() {
    return index;
  }

  @Override
  public int compareTo(PositionArgumentCoordinate that) {
    return Integer.compare(this.index, that.index);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PositionArgumentCoordinate that)) {
      return false;
    }
    return getIndex() == that.getIndex();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getIndex());
  }

  @Override
  public String toString() {
    return "@" + Integer.toString(index);
  }
}
