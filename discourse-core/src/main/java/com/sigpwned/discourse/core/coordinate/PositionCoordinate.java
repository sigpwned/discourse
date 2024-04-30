/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.coordinate;

import java.util.Comparator;
import java.util.Objects;

/**
 * A {@link Coordinate} that represents a position in a sequence.
 */
public final class PositionCoordinate extends Coordinate implements Comparable<PositionCoordinate> {

  public static final PositionCoordinate ZERO = new PositionCoordinate(0);

  public static PositionCoordinate of(int index) {
    if (index == 0) {
      return ZERO;
    }
    return new PositionCoordinate(index);
  }

  private final int index;

  public PositionCoordinate(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index is negative");
    }
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
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PositionCoordinate other = (PositionCoordinate) obj;
    return index == other.index;
  }

  @Override
  public String toString() {
    return "position " + index;
  }

  public static final Comparator<PositionCoordinate> COMPARATOR = Comparator.comparingInt(
      PositionCoordinate::getIndex);

  @Override
  public int compareTo(PositionCoordinate that) {
    return COMPARATOR.compare(this, that);
  }
}
