/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.args.coordinate;

import java.util.Objects;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.args.Coordinate;

public final class PositionalCoordinate extends Coordinate
    implements Comparable<PositionalCoordinate> {
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

  @Override
  public int compareTo(PositionalCoordinate that) {
    return Integer.compare(this.position, that.position);
  }
}
