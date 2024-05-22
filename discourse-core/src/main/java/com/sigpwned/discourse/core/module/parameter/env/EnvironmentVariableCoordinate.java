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
package com.sigpwned.discourse.core.module.parameter.env;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Coordinate;

public class EnvironmentVariableCoordinate extends Coordinate {
  private final String variableName;

  public EnvironmentVariableCoordinate(String variableName) {
    this.variableName = requireNonNull(variableName);
  }

  public String getVariableName() {
    return variableName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(variableName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnvironmentVariableCoordinate other = (EnvironmentVariableCoordinate) obj;
    return Objects.equals(variableName, other.variableName);
  }

  @Override
  public String toString() {
    return "EnvironmentVariableCoordinate[" + variableName + "]";
  }
}
