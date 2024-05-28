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
package com.sigpwned.discourse.core.module.parameter.systemproperty;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Coordinate;

public class SystemPropertyCoordinate extends Coordinate {
  private final String propertyName;

  public SystemPropertyCoordinate(String propertyName) {
    this.propertyName = requireNonNull(propertyName);
  }

  public String getPropertyName() {
    return propertyName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SystemPropertyCoordinate other = (SystemPropertyCoordinate) obj;
    return Objects.equals(propertyName, other.propertyName);
  }

  @Override
  public String toString() {
    return "SystemPropertyCoordinate[" + propertyName + "]";
  }
}
