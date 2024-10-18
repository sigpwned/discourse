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
